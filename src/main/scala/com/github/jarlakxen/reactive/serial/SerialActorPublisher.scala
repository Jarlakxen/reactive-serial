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

private[serial] class SerialActorPublisher(
    port: Port,
    baudRate: Int,
    bufferSize: Int) extends ActorPublisher[ByteString] with ActorLogging {

  val readBuffer = new Array[Byte](bufferSize)

  override def preStart(): Unit = {
    port.open(baudRate).recover {
      case ex =>
        log.error(ex, "Cannot start stream")
        port.close
        onErrorThenStop(ex)
    }
  }

  override def receive = {
    case ActorPublisherMessage.Request(requestedElements) =>
      log.debug(s"Requesting $requestedElements elements")
      readDemandedItems(requestedElements)
    case ActorPublisherMessage.Cancel | ActorPublisherMessage.SubscriptionTimeoutExceeded =>
      log.debug("Canceling streaming")
      port.close
      onCompleteThenStop()
  }

  private def tryReadingSingleElement(): Try[ByteString] =
    port.read(readBuffer).map(length => ByteString.fromArray(readBuffer, 0, length))

  @tailrec
  private def readDemandedItems(requestedElements: Long): Unit = {
    tryReadingSingleElement() match {
      case Success(data) if requestedElements > 0 =>
        onNext(data)
        readDemandedItems(requestedElements - 1)
      case Failure(ex) =>
        onErrorThenStop(ex)
      case _ => // Do Nothing
    }
  }
}