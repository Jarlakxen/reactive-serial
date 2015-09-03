package com.github.jarlakxen.reactive.serial

import scala.concurrent.ExecutionContext._
import scala.concurrent.duration._
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.specification.core._
import org.specs2.specification.mutable._
import org.specs2.runner.JUnitRunner
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl._
import akka.stream.actor.ActorPublisherMessage.Request
import akka.testkit._
import akka.util.ByteString
import org.scalamock.specs2.MockContext

/**
 * @author fviale
 */

@RunWith(classOf[JUnitRunner])
class ReactiveSerialSpec extends Specification with Environment {
  sequential

  class MockablePort extends Port(null)
  
  def is(env: Env) = {

    "ReactiveSerial specification" >> {

      "We get 1 command from 1 byte sequence" in new AkkaContext with MockContext {

        implicit val materializer = ActorMaterializer()

        val port = mock[MockablePort]
        
      }
    }
  }

}