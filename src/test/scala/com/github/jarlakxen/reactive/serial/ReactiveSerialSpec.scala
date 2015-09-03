package com.github.jarlakxen.reactive.serial

import scala.concurrent.ExecutionContext._
import scala.concurrent.duration._
import scala.util.{ Try, Failure, Success }
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.specs2.specification.core._
import org.specs2.specification.mutable._
import org.specs2.runner.JUnitRunner
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl._
import akka.stream.actor.ActorPublisherMessage.Request
import akka.testkit._
import akka.util.ByteString
import org.mockito.Matchers._
import akka.stream.actor.ZeroRequestStrategy

/**
 * @author fviale
 */

@RunWith(classOf[JUnitRunner])
class ReactiveSerialSpec extends Specification with Mockito with Environment {
  sequential

  def afterThan(duration: Duration) = after(duration)

  def is(env: Env) = {

    "ReactiveSerial specification" >> {

      "test a stream using the actorPublisher" in new AkkaContext {

        implicit val materializer = ActorMaterializer()

        val msg1 = ByteString(1, 2, 3)
        val msg2 = ByteString(4, 5, 6)

        val port = spy(new Port(null))

        doReturn(Success(())).when(port).open(anyInt)
        doReturn(Success(())).when(port).close

        val streamProbe = Source.actorPublisher(ReactiveSerial(port).actorPublisherProps(100)).runWith(TestSink.probe[ByteString])

        doAnswer { buffer => msg1.copyToArray(buffer.asInstanceOf[Array[Byte]]); Success(msg1.length) }.when(port).read(any[Array[Byte]])
        streamProbe.request(1).expectNext(msg1)
        doAnswer { buffer => msg2.copyToArray(buffer.asInstanceOf[Array[Byte]]); Success(msg2.length) }.when(port).read(any[Array[Byte]])
        streamProbe.request(1).expectNext(msg2)

        streamProbe.cancel()

        there was one(port).close
      }

      "test a stream using the actorSubscriber" in new AkkaContext {

        implicit val materializer = ActorMaterializer()

        val msg1 = ByteString(1, 2, 3)
        val msg2 = ByteString(4, 5, 6)

        val port = mock[Port]

        doReturn(Success(())).when(port).open(anyInt)
        doReturn(Success(())).when(port).close

        port.write(any[ByteString]) returns Success(3)

        val (pub, sub) = TestSource.probe[ByteString]
          .toMat(Sink.actorSubscriber[ByteString](ReactiveSerial(port).actorSubscriberProps(ZeroRequestStrategy)))(Keep.both)
          .run()

        pub.sendNext(msg1)
        pub.sendNext(msg2)

        there was afterThan(10.millis).one(port).write(msg1) andThen afterThan(10.millis).one(port).write(msg2)
      }
    }
  }

}