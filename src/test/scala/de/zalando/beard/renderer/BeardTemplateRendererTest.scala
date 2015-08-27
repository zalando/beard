package de.zalando.beard.renderer

import de.zalando.beard.parser.BeardTemplateParser
import org.scalatest.{FunSpec, Matchers}

import scala.collection.immutable.Seq
import scala.io.Source

/**
 * @author dpersa
 */
class BeardTemplateRendererTest extends FunSpec with Matchers {

  val partial = BeardTemplateParser {
    Source.fromInputStream(getClass.getResourceAsStream(s"/templates/_partial.beard")).mkString
  }

  val renderer = new BeardTemplateRenderer(Map("partial" -> partial))

  describe("BeardTemplateRendererTest") {

    it("should render a template with a simple identifier") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/identifier-interpolation.beard")).mkString
      }

      renderer.render(template, Map("name" -> "Gigi")) should
        be("<div>Gigi</div>")
    }

    it("should render a template with a compound identifier") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/compound-identifier-interpolation.beard")).mkString
      }

      renderer.render(template, Map("user" -> Map("name" -> "Gigi", "email" -> "gigi@gicu.com"))) should
        be("<div>gigi@gicu.com</div>")
    }

    it("should render a template with a for statement") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-statement.beard")).mkString
      }

      renderer.render(template, Map("users" -> Seq(Map("name" -> "Gigi")))) should
        be("<div>Hello</div>")
    }

    it("should render a template with a complex for statement") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-complex-statement.beard")).mkString
      }

      renderer.render(template, Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu")))) should
        be("<div>Gigi</div><div>Gicu</div>")
    }

    it("should render a template with a render statement") {

      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/layout-with-partial.beard")).mkString
      }

      renderer.render(template, Map("example" -> Map("title" -> "Title", "presentations" -> Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
        Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2"))))) should not be("")
    }
  }
}
