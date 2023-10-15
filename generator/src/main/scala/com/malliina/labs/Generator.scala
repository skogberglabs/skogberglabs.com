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
      "index.html" -> pages.index,
      "thepill/index.html" -> pages.pill,
      "pillalarm/support/index.html" -> pages.support,
      "pillalarm/privacy/index.html" -> pages.privacy,
      "404.html" -> pages.notFound
    )
    pageMap.foreach { case (file, page) => page.write(dist.resolve(file)) }
    NetlifyClient.writeHeaders(dist)
