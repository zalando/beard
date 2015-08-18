package de.zalando.beard.renderer

import de.zalando.beard.ast._
import de.zalando.beard.parser.BeardTemplateParser
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

/**
 * @author dpersa
 */
class BeardTemplateRendererTest extends FunSpec with Matchers {

  val renderer = new BeardTemplateRenderer

  describe("BeardTemplateRendererTest") {

    it("should render") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for.beard")).mkString
      }

      renderer.render(template, Map("name" -> "world")) should be("<div>Hello</div>")
    }
  }
}
