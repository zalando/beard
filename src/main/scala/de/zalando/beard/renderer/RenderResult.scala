package de.zalando.beard.renderer

import java.io.StringWriter

/**
 * @author dpersa
 */
trait RenderResult[T] {
  def write(string: String): Unit

  def complete(): Unit = {}

  def result: T
}

case class StringWriterRenderResult() extends RenderResult[StringWriter] {

  val stringWriter = new StringWriter()

  override def write(renderedChunk: String): Unit = {
    stringWriter.write(renderedChunk)
  }

  override def result = stringWriter
}
