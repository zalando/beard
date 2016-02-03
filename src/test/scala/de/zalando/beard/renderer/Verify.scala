package de.zalando.beard.renderer

import com.typesafe.scalalogging.LazyLogging
import de.zalando.beard.ast.BeardTemplate
import org.scalatest.Matchers

import scala.io.Source

/**
  * @author dpersa
  */
class Verify(val renderer: BeardTemplateRenderer) extends Matchers with LazyLogging {

  def whitespaces(template: BeardTemplate, expectedTemplate: String, context: Map[String, Any] = Map()) = {
    val result = renderer.render(template, StringWriterRenderResult(), context)

    val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/$expectedTemplate.rendered")).mkString

    logger.debug("Result: ")
    logger.debug(result.toString)

    result.toString should be(expected)
  }
}
