package de.zalando.beard.renderer

import xml.Utility

/**
  * @autor cesarla
  */
trait EscapeStrategy {

  def escape(text: String) : String

}

class XMLEscapeStrategy extends EscapeStrategy {

  override def escape(value: String) : String = scala.xml.Utility.escape(value)

}

class VanillaEscapeStrategy extends EscapeStrategy {

  override def escape(value: String) : String = value

}

object EscapeStrategy {
  lazy val xml = html
  lazy val html = new XMLEscapeStrategy()
  lazy val vanilla = new VanillaEscapeStrategy()
}