import com.malliina.sbtutils.SbtUtils

inThisBuild(
  Seq(
    organization := "com.malliina",
    version := "1.0.0",
    scalaVersion := "3.2.2"
  )
)

val Dev = config("dev")
val Prod = config("prod")
val build = taskKey[Unit]("Builds app")
val deploy = inputKey[Unit]("Deploys the site")
val siteDir = settingKey[File]("Site directory")

val scalatagsVersion = "0.12.0"

val frontend = project
  .in(file("frontend"))
  .enablePlugins(NodeJsPlugin, RollupPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % scalatagsVersion
    )
  )

val generator = project
  .in(file("generator"))
  .enablePlugins(NetlifyPlugin)
  .settings(
    scalajsProject := frontend,
    copyFolders += ((Compile / resourceDirectory).value / "public").toPath,
    libraryDependencies ++= SbtUtils.loggingDeps ++ Seq(
      "com.malliina" %% "primitives" % "3.4.5",
      "com.malliina" %% "common-build" % "1.6.19",
      "com.lihaoyi" %% "scalatags" % scalatagsVersion
    )
  )

val www = project
  .in(file("."))
  .aggregate(frontend, generator)

Global / onChangedBuildSource := ReloadOnSourceChanges
