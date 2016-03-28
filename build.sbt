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
  "net.sf.ehcache" % "ehcache" % "2.7.4",
  "javax.transaction" % "jta" % "1.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % "test",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8")

libraryDependencies += "org.specs2" % "specs2_2.9.1" % "1.8"
libraryDependencies += "org.scalafx" % "scalafx_2.11" % "8.0.60-R9"
libraryDependencies += "org.scalamock" % "scalamock-scalatest-support_2.11" % "3.2.2"
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.19"

