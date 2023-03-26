package com.malliina.labs

import buildinfo.BuildInfo

import java.nio.file.{Files, Path, Paths}

object Generator:
  val log = AppLogger(getClass)

  def main(args: Array[String]): Unit =
    generate(BuildInfo.isProd, BuildInfo.siteDir.toPath)

  def generate(isProd: Boolean, dist: Path) =
    Files.createDirectories(dist)
    val pages = Pages(isProd)
    val pageMap = Map(
      pages.index -> "index.html",
      pages.pill -> "thepill/index.html",
      pages.support -> "pillalarm/support/index.html",
      pages.privacy -> "pillalarm/privacy/index.html"
    )
    pageMap.foreach { case (page, file) => page.write(dist.resolve(file)) }
    NetlifyClient.writeHeaders(dist)
