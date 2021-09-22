package com.malliina.labs

import org.slf4j.{Logger, LoggerFactory}

object AppLogger {
  def apply(cls: Class[_]): Logger = {
    val name = cls.getName.reverse.dropWhile(_ == '$').reverse
    LoggerFactory.getLogger(name)
  }
}
