name := "Journey"

version := "1.0"

scalaVersion := "2.11.8"


sbtVersion := "0.13.5"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "com.typesafe.akka" %% "akka-remote" % "2.4.0",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.0",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.0",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.0",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.0",
  "com.typesafe.akka" %% "akka-contrib" % "2.4.0",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.2.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % "test",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8")