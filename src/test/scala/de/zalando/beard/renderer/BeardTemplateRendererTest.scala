package de.zalando.beard.renderer

import de.zalando.beard.parser.BeardTemplateParser
import org.scalatest.{FunSpec, Matchers}

import scala.collection.immutable.Seq
import scala.io.Source

/**
 * @author dpersa
 */
class BeardTemplateRendererTest extends FunSpec with Matchers {

  val templateCompiler = DefaultTemplateCompiler
  val renderer = new BeardTemplateRenderer(templateCompiler)

  describe("BeardTemplateRendererTest") {

    it("should render a template with a simple identifier") {
      templateCompiler.compile(TemplateName("/templates/identifier-interpolation.beard"))
        .map { template =>
        renderer.render(template, Map("name" -> "Gigi")).toString() should
          be("<div>Gigi</div>")
      }
      ()
    }

    it("should render a template with a compound identifier") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/compound-identifier-interpolation.beard")).mkString
      }

      renderer.render(template, Map("user" -> Map("name" -> "Gigi", "email" -> "gigi@gicu.com")))
        .toString() should be("<div>gigi@gicu.com</div>")
    }

    it("should render a template with a for statement") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-statement.beard")).mkString
      }

      renderer.render(template, Map("users" -> Seq(Map("name" -> "Gigi"))))
        .toString() should be("<div>Hello</div>")
    }

    it("should render a template with a complex for statement") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-complex-statement.beard")).mkString
      }

      renderer.render(template, Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))))
        .toString() should be("<div>Gigi</div><div>Gicu</div>")
    }

    it("should render a template with a render statement") {

      val r = templateCompiler.compile(TemplateName("/templates/layout-with-partial.beard"))
        .map { template =>

        renderer.render(template, Map("example" -> Map("title" -> "Title", "presentations" ->
          Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
            Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2")))))
          .toString() should not be ("")
      }
    }

    it ("should render a template with an export statement") {
      pending
    }

    it ("should render a template with a block statement") {
      pending
    }

    it ("should render a template with a contentFor statement") {
      pending
    }
  }
}
