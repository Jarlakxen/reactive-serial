package com.github.jarlakxen.reactive.serial

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import akka.actor._
import akka.stream.actor.{ RequestStrategy, ActorPublisher, ActorSubscriber }
import akka.util.ByteString
import com.fazecast.jSerialComm.SerialPort
import org.reactivestreams.{ Publisher, Subscriber }

/**
 * @author fviale
 */

case class ReactiveSerial(port: SerialPort) {

  port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0)

  def publisher(bufferSize: Int)(implicit actorSystem: ActorSystem): Publisher[ByteString] =
    ActorPublisher[ByteString](actorPublisher(bufferSize))

  def actorPublisher(bufferSize: Int)(implicit actorSystem: ActorSystem): ActorRef =
    actorSystem.actorOf(actorPublisherProps(bufferSize))

  def actorPublisherProps(bufferSize: Int): Props =
    Props(new SerialActorPublisher(port, bufferSize))

    
  def subscriber(requestStrategyProvider: () => RequestStrategy)(implicit actorSystem: ActorSystem): Subscriber[ByteString] =
    ActorSubscriber[ByteString](actorSubscriber(requestStrategyProvider))

  def actorSubscriber(requestStrategyProvider: () => RequestStrategy)(implicit actorSystem: ActorSystem): ActorRef =
    actorSystem.actorOf(actorSubscriberProps(requestStrategyProvider))

  def actorSubscriberProps(requestStrategyProvider: () => RequestStrategy): Props =
    Props(new SerialActorSubscriber(port, requestStrategyProvider))

}

object ReactiveSerial {

  def serialPorts = SerialPort.getCommPorts().toList

  def serialPort(portDescriptor: String) = SerialPort.getCommPort(portDescriptor)

  def apply(): ReactiveSerial = {
    val ports = serialPorts
    if (ports.nonEmpty) {
      ReactiveSerial(ports.head)
    } else {
      throw new RuntimeException("No available ports")
    }
  }

  def apply(portDescriptor: String): ReactiveSerial = ReactiveSerial(serialPort(portDescriptor))

}