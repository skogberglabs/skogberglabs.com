scalaVersion := "2.12.17"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.4.0",
  "com.malliina" % "live-reload" % "0.5.0",
  "org.scala-js" % "sbt-scalajs" % "1.13.0",
  "ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1",
  "org.scalameta" % "sbt-scalafmt" % "2.5.0"
) map addSbtPlugin
