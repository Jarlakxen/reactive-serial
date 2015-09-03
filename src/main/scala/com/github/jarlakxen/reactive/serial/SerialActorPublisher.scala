package com.github.jarlakxen.reactive.serial

import akka.actor.{ PoisonPill, ActorLogging }
import akka.stream.actor.{ ActorPublisher, ActorPublisherMessage }
import akka.util.ByteString
import scala.annotation.tailrec
import scala.util.{ Try, Success, Failure }
import java.nio.ByteBuffer

/**
 * @author fviale
 */

private[serial] class SerialActorPublisher(port: Port, bufferSize: Int) extends ActorPublisher[ByteString] with ActorLogging {

  val readBuffer = new Array[Byte](bufferSize)

  override def preStart(): Unit = {
    port.open.recover {
      case ex =>
        log.error(ex, "Cannot start stream")
        self ! PoisonPill
    }
  }

  override def receive = {
    case ActorPublisherMessage.Request(_) => readDemandedItems()
    case ActorPublisherMessage.Cancel | ActorPublisherMessage.SubscriptionTimeoutExceeded =>
      port.close
      context.stop(self)
  }

  private def demand_? : Boolean = totalDemand > 0

  private def tryReadingSingleElement(): Try[ByteString] =
    port.read(readBuffer).map(length => ByteString.fromArray(readBuffer, 0, length))

  @tailrec
  private def readDemandedItems(): Unit = {
    tryReadingSingleElement() match {
      case Success(data) =>
        onNext(data)
        if (demand_?) readDemandedItems()
      case Failure(ex) =>
        onError(ex)
    }
  }
}