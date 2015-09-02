package com.github.jarlakxen.reactive.serial

import akka.actor.ActorLogging
import akka.stream.actor.{ ActorSubscriber, ActorSubscriberMessage, RequestStrategy }
import akka.util.ByteString
import com.fazecast.jSerialComm.SerialPort

/**
 * @author fviale
 */

private[serial] class SerialActorSubscriber(
  port: SerialPort,
  requestStrategyProvider: () => RequestStrategy)
    extends ActorSubscriber with ActorLogging {

  override protected val requestStrategy = requestStrategyProvider()

  override def preStart(): Unit = {
    if (!port.openPort) {
      log.error(s"Cannot open port '${port.getDescriptivePortName}'")
      cancel()
    }
  }

  def receive = {
    case ActorSubscriberMessage.OnNext(data: ByteString) =>
      process(data)

    case ActorSubscriberMessage.OnError(ex) =>
      handleError(ex)

    case ActorSubscriberMessage.OnComplete =>
      stop()

  }

  private def process(data: ByteString): Unit = {
    if (port.writeBytes(data.toArray, data.length) == -1) {
      log.error(s"There was an error writing to the port '${port.getDescriptivePortName}'")
      cancel()
    }
  }

  private def handleError(ex: Throwable): Unit = {
    log.error("Stopping serial communication due to fatal error.", ex)
    stop()
  }

  private def stop(): Unit = {
    port.closePort
    context.stop(self)
  }

}