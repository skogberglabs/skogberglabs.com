import com.malliina.sbtutils.SbtUtils

import java.nio.file.{Files, StandardCopyOption}
import complete.DefaultParsers.spaceDelimited

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

val scalatagsVersion = GeneratorClientPlugin.scalatagsVersion

val frontend = project
  .in(file("frontend"))
  .enablePlugins(GeneratorClientPlugin)
  .settings(
    siteDir := (ThisBuild / baseDirectory).value / "target" / "site",
    Compile / fullOptJS / build := (Compile / fullOptJS / webpack).value.map { af =>
      val destDir = siteDir.value
      val base = (Compile / npmUpdate / crossTarget).value
      val rel = af.data.relativeTo(base).get
      val dest = (destDir / rel.toString).toPath
      Files.createDirectories(dest.getParent)
      sLog.value.info(s"Write $dest ${af.metadata}")
      Files.copy(af.data.toPath, dest, StandardCopyOption.REPLACE_EXISTING).toFile
    },
    Compile / fastOptJS / build := (Compile / fastOptJS / webpack).value.map { af =>
      val destDir = siteDir.value
      val base = (Compile / npmUpdate / crossTarget).value
      val rel = af.data.relativeTo(base).get
      val dest = (destDir / rel.toString).toPath
      Files.createDirectories(dest.getParent)
      sLog.value.info(
        s"Write $dest from ${af.data} ${af.metadata} ${af.metadata.get(BundlerFileTypeAttr)}"
      )
      Files.copy(af.data.toPath, dest, StandardCopyOption.REPLACE_EXISTING).toFile
    },
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      ("com.lihaoyi" %%% "scalatags" % scalatagsVersion).cross(CrossVersion.for3Use2_13)
    ),
    watchSources += WatchSource(baseDirectory.value / "src", "*.scala", HiddenFileFilter),
    webpack / version := "5.65.0",
    webpackCliVersion := "4.9.1",
    startWebpackDevServer / version := "4.5.0",
    Compile / fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),
    Compile / fullOptJS / webpackBundlingMode := BundlingMode.Application,
    webpackEmitSourceMaps := false,
    Compile / npmDependencies ++= Seq(
      "@popperjs/core" -> "2.11.6",
      "bootstrap" -> "5.2.3"
    ),
    Compile / npmDevDependencies ++= Seq(
      "autoprefixer" -> "10.4.13",
      "cssnano" -> "5.1.15",
      "css-loader" -> "6.7.3",
      "less" -> "4.1.3",
      "less-loader" -> "11.1.0",
      "mini-css-extract-plugin" -> "2.7.2",
      "postcss" -> "8.4.21",
      "postcss-import" -> "15.1.0",
      "postcss-loader" -> "7.0.2",
      "postcss-preset-env" -> "8.0.1",
      "style-loader" -> "3.3.1",
      "webpack-merge" -> "5.8.0"
    )
  )

val generator = project
  .in(file("generator"))
  .enablePlugins(LiveReloadPlugin)
  .settings(
    libraryDependencies ++= SbtUtils.loggingDeps ++ Seq(
      "com.malliina" %% "primitives" % "3.4.0",
      ("com.lihaoyi" %% "scalatags" % scalatagsVersion).cross(CrossVersion.for3Use2_13)
    ),
    liveReloadRoot := (frontend / siteDir).value.toPath,
    refreshBrowsers := refreshBrowsers.triggeredBy(Dev / build).value,
    watchSources := watchSources.value ++ Def.taskDyn(frontend / watchSources).value,
    Prod / build := (Compile / run)
      .toTask(s" prod target/site")
      .dependsOn(frontend / Compile / fullOptJS / build)
      .value,
    Dev / build := (Compile / run)
      .toTask(s" dev target/site")
      .dependsOn(frontend / Compile / fastOptJS / build, Def.task(reloader.value.start()))
      .value,
    deploy := {
      val args = spaceDelimited("<arg>").parsed
      NPM.runProcessSync(
        args.mkString(" "),
        (ThisBuild / baseDirectory).value,
        streams.value.log
      )
    },
    Prod / deploy := deploy.toTask(" netlify deploy --prod").dependsOn(Prod / build).value,
    Dev / deploy := deploy.toTask(" netlify deploy").dependsOn(Dev / build).value
  )

val www = project
  .in(file("."))
  .aggregate(frontend, generator)
  .settings(
    build := (generator / Dev / build).value
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
