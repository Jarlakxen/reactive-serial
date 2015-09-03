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

case class ReactiveSerial(port: Port) {

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

  lazy val ports = SerialPort.getCommPorts().map(new Port(_)).toList

  def port(descriptor: String) = new Port(SerialPort.getCommPort(descriptor))

  def apply(): ReactiveSerial = {
    if (ports.nonEmpty) {
      ReactiveSerial(ports.head)
    } else {
      throw new RuntimeException("No available ports")
    }
  }

  def apply(descriptor: String): ReactiveSerial = ReactiveSerial(port(descriptor))

}