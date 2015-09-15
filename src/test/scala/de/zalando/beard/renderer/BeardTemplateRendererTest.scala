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
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, Map("name" -> "Gigi"))
        renderResult.result.toString should be("<div>Gigi</div>")
      }
      ()
    }

    it("should render a template with a compound identifier") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/compound-identifier-interpolation.beard")).mkString
      }

      val renderResult = StringWriterRenderResult()
      renderer.render(template, renderResult, Map("user" -> Map("name" -> "Gigi", "email" -> "gigi@gicu.com")))

      renderResult.result.toString() should be("<div>gigi@gicu.com</div>")
    }

    it("should render a template with a for statement") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-statement.beard")).mkString
      }

      val renderResult = StringWriterRenderResult()

      renderer.render(template, renderResult, Map("users" -> Seq(Map("name" -> "Gigi"))))
      renderResult.result.toString() should be("<div>Hello</div>")
    }

    it("should render a template with a complex for statement") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-complex-statement.beard")).mkString
      }

      val renderResult = StringWriterRenderResult()
      renderer.render(template, renderResult, Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))))
      renderResult.result.toString() should be("<div>Gigi</div><div>Gicu</div>")
    }

    it("should render a template with a render statement") {

      val r = templateCompiler.compile(TemplateName("/templates/layout-with-partial.beard"))
        .map { template =>
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, Map("example" -> Map("title" -> "Title", "presentations" ->
          Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
            Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2")))))
        renderResult.result.toString() should not be ("")
      }
    }

    it("should render a template with an export statement") {
      pending
    }

    it("should render a template with a block statement") {
      pending
    }

    it("should render a template with a contentFor statement") {
      pending
    }
  }
}
