# Reactive Streams for Serial Communication

[Reactive Streams](http://www.reactive-streams.org) wrapper for [jSerialComm](http://fazecast.github.io/jSerialComm/). 

Available for 2.11 and 2.12:

````scala
resolvers += Resolver.bintrayRepo("jarlakxen", "maven")
libraryDependencies += "com.github.jarlakxen" %% "reactive-serial" % "1.4"
````

Example usage
----

```Scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.github.jarlakxen.reactive.serial.ReactiveSerial

implicit val actorSystem = ActorSystem("ReactiveSerial")
implicit val materializer = ActorMaterializer()

val serialPort = ReactiveSerial.port("/dev/ttyUSB0")

val serial = ReactiveSerial(port = serialPort, baudRate = 57600)

val publisher: Publisher[ByteString] = serial.publisher(bufferSize=100)
val subscriber: Subscriber[ByteString] = serial.subscriber(requestStrategyProvider=ZeroRequestStrategy)

Source.fromPublisher(publisher).map(_.message().toUpperCase).to(Sink.fromSubscriber(subscriber)).run()
```
