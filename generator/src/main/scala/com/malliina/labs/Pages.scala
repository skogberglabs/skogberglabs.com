package com.malliina.labs

import com.malliina.http.FullUrl
import com.malliina.labs.Pages.*
import com.malliina.live.LiveReload
import com.malliina.assets.HashedAssets
import scalatags.Text.all.*
import scalatags.text.Builder

import java.nio.file.{Files, Path}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.IteratorHasAsScala

object Pages:
  implicit val fullUrl: AttrValue[FullUrl] = attrType[FullUrl](_.url)

  val empty = modifier()
  val time = tag("time")
  val titleTag = tag("title")

  val datetime = attr("datetime")
  val property = attr("property")

  def attrType[T](stringify: T => String): AttrValue[T] = (t: Builder, a: Attr, v: T) =>
    t.setAttr(a.name, Builder.GenericAttrValueSource(stringify(v)))

class Pages(isProd: Boolean):
  val globalDescription = "Skogberg Labs."

  val section = tag("section")

  val scripts =
    if isProd then scriptAt("frontend.js", defer)
    else
      modifier(
        scriptAt("frontend.js"),
        script(src := LiveReload.script)
      )

  def index = base("Skogberg Labs")(empty)
  def pill = base("The Pill")(empty)
  def support = base("Support")(
    section(`class` := "section support")(
      h1(`class` := "title")("Support"),
      p("Should you have any questions, you may:"),
      ul(
        li("Email ", a(href := "mailto:info@skogberglabs.com")("info@skogberglabs.com")),
        li("Contact me on ", a(href := "https://twitter.com/skogberglabs")("Twitter"))
      )
    )
  )
  def privacy = base("Privacy policy")(
    section(`class` := "section privacy")(
      h1(`class` := "title")("Privacy policy"),
      p("Alarms and any related data is only stored on the device."),
      p(
        "PillAlarm collects no user information. No information is sent anywhere. There is no tracking."
      )
    )
  )

  def notFound = base("Not found")(
    div(`class` := "centered")("Not found.")
  )

  def base(titleText: String)(contents: Modifier*): TagPage = TagPage(
    html(lang := "en")(
      head(
        titleTag(titleText),
        meta(charset := "UTF-8"),
        meta(
          name := "viewport",
          content := "width=device-width, initial-scale=1.0"
        ),
        link(rel := "shortcut icon", `type` := "image/png", href := findAsset("img/jag-16x16.png")),
        meta(name := "description", content := globalDescription),
        meta(name := "keywords", content := "Skogberg Labs"),
        meta(name := "twitter:card", content := "summary"),
        meta(name := "twitter:site", content := "@SkogbergLabs"),
        meta(name := "twitter:creator", content := "@SkogbergLabs"),
        meta(property := "og:title", content := titleText),
        meta(property := "og:description", content := globalDescription),
        styleAt("styles.css"),
        styleAt("fonts.css")
      ),
      body(
        contents :+ scripts
      )
    )
  )

  def format(date: LocalDate) =
    val localDate = DateTimeFormatter.ISO_LOCAL_DATE.format(date)
    time(datetime := localDate)(localDate)

  def styleAt(file: String) = link(rel := "stylesheet", href := findAsset(file))

  def scriptAt(file: String, modifiers: Modifier*) = script(src := findAsset(file), modifiers)

  def inlineOrAsset(at: String) = HashedAssets.dataUris.getOrElse(at, findAsset(at))
  def findAsset(at: String) =
    HashedAssets.assets.get(at).map(p => s"/$p").getOrElse(fail(s"Not found: '$at'."))

  def fail(message: String) = throw new Exception(message)
