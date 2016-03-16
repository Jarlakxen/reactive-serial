# Reactive Streams for Serial Communication

[Reactive Streams](http://www.reactive-streams.org) wrapper for [jSerialComm](http://fazecast.github.io/jSerialComm/). 

Available at OSS Sonatype for 2.11:

````scala
libraryDependencies += "com.github.jarlakxen" %% "reactive-serial" % "1.3"
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

Source(publisher).map(_.message().toUpperCase).to(Sink(subscriber)).run()
```
