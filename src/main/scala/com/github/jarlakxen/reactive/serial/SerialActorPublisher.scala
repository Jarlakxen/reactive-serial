package com.github.jarlakxen.reactive.serial

import akka.actor.ActorLogging
import akka.stream.actor.{ ActorPublisher, ActorPublisherMessage }
import akka.util.ByteString
import com.fazecast.jSerialComm.SerialPort
import scala.annotation.tailrec
import scala.util.{ Try, Success, Failure }
import java.nio.ByteBuffer

/**
 * @author fviale
 */

private[serial] class SerialActorPublisher(port: SerialPort, bufferSize: Int) extends ActorPublisher[ByteString] with ActorLogging {

  val readBuffer = new Array[Byte](bufferSize)

  override def preStart(): Unit = {
    if (!port.openPort) {
      log.error(s"Cannot open port '${port.getDescriptivePortName}'")

    }
  }

  override def receive = {
    case ActorPublisherMessage.Request(_) => readDemandedItems()
    case ActorPublisherMessage.Cancel | ActorPublisherMessage.SubscriptionTimeoutExceeded =>
      port.closePort
      context.stop(self)
  }

  private def demand_? : Boolean = totalDemand > 0

  private def tryReadingSingleElement(): Try[ByteString] = {

    val length = port.readBytes(readBuffer, bufferSize)

    if (length == -1) {
      Failure(new RuntimeException(s"There was an error reading the port '${port.getDescriptivePortName}'"))
    } else {
      Success(ByteString.fromArray(readBuffer, 0, length))
    }
  }

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