package com.github.jarlakxen.reactive.serial

import akka.actor.{ PoisonPill, ActorLogging }
import akka.stream.actor.{ ActorSubscriber, ActorSubscriberMessage, RequestStrategy }
import akka.util.ByteString
import scala.util.{ Try, Success, Failure }

/**
 * @author fviale
 */

private[serial] class SerialActorSubscriber(
  port: Port,
  requestStrategyProvider: RequestStrategy)
    extends ActorSubscriber with ActorLogging {

  override protected val requestStrategy = requestStrategyProvider

  override def preStart(): Unit = {
    port.open match {
      case Success(_) =>
        request(1)
      case Failure(ex) =>
        log.error(ex, "Cannot start stream")
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
    port.write(data) match {
      case Success(_) =>
        request(1)
      case Failure(ex) =>
        log.error(ex, "Cannot continue with stream")
        cancel()
    }
  }

  private def handleError(ex: Throwable): Unit = {
    log.error("Stopping serial communication due to fatal error.", ex)
    stop()
  }

  private def stop(): Unit = {
    port.close
    context.stop(self)
  }

}