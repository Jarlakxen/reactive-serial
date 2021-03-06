import sbt.Keys._

// ··· Settings ···


// ··· Project Info ···

name := "reactive-serial"

organization := "com.github.jarlakxen"

crossScalaVersions := Seq("2.11.8", "2.12.1")

scalaVersion <<= (crossScalaVersions) { versions => versions.head }

fork in run  := true

publishArtifact in Test := false

licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))

// ··· Project Enviroment ···


// ··· Project Options ···

scalacOptions ++= Seq(
    "-encoding",
    "utf8",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-unchecked",
    "-deprecation"
)

scalacOptions in Test ++= Seq("-Yrangepos")

// ··· Project Dependancies ···

val vAkka         = "2.4.14"
val vjSerialComm  = "1.3.11"
val vSlf4J        = "1.7.22"
val vLogback      = "1.1.8"
val vSpec2        = "3.8.6"
val vJUnit        = "4.12"

libraryDependencies ++= Seq(
  // --- Akka --
  "com.typesafe.akka"             %% "akka-actor"                         % vAkka         %  "provided",
  "com.typesafe.akka"             %% "akka-stream"                        % vAkka         %  "provided",
  // --- Akka --
  "com.fazecast"                  % "jSerialComm"                         % vjSerialComm,
  // --- Logger ---
  "org.slf4j"                     %  "slf4j-api"                          % vSlf4J,
  "ch.qos.logback"                %  "logback-classic"                    % vLogback      %  "test",
  // --- Testing ---
  "com.typesafe.akka"             %% "akka-slf4j"                         % vAkka         %  "test",
  "com.typesafe.akka"             %% "akka-stream-testkit"                % vAkka         %  "test",
  "org.specs2"                    %% "specs2-core"                        % vSpec2        %  "test",
  "org.specs2"                    %% "specs2-mock"                        % vSpec2        %  "test",
  "org.specs2"                    %% "specs2-junit"                       % vSpec2        %  "test",
  "junit"                         %  "junit"                              % vJUnit        %  "test"
)

pomExtra := (
  <url>https://github.com/Jarlakxen/reactive-serial</url>
  <licenses>
    <license>
      <name>Apache License v2</name>
      <url>https://github.com/Jarlakxen/reactive-serial/blob/master/LICENSE</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/Jarlakxen/reactive-serial</url>
    <connection>scm:git:git@github.com:Jarlakxen/reactive-serial.git</connection>
    <developerConnection>scm:git:git@github.com:Jarlakxen/reactive-serial.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>Jarlakxen</id>
      <name>Facundo Viale</name>
      <url>https://github.com/Jarlakxen/reactive-serial</url>
    </developer>
  </developers>
)
