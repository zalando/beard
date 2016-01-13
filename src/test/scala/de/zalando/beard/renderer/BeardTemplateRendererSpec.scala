package de.zalando.beard.renderer

import de.zalando.beard.parser.BeardTemplateParser
import org.scalatest.{FunSpec, Matchers}

import scala.collection.immutable.Seq
import scala.io.Source

/**
 * @author dpersa
 */
class BeardTemplateRendererSpec extends FunSpec with Matchers {

  val templateCompiler = DefaultTemplateCompiler
  val renderer = new BeardTemplateRenderer(templateCompiler)

  describe("BeardTemplateRendererTest") {

    it("should render a template with a simple identifier") {
      templateCompiler.compile(TemplateName("/templates/identifier-interpolation.beard"))
        .map { template =>
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, Map("name" -> "Gigi"))
          renderResult.result.toString should be("<div>Gigi</div>")
        }.isSuccess shouldBe true
    }

    it("should render a scala template with a simple identifier") {
      templateCompiler.compile(TemplateName("/templates/scala.beard"))
        .map { template =>
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, Map("name" -> "Gigi"))
          renderResult.result.toString should be("object Test extends App {\n  println(\"Gigi\")\n}")
        }.isSuccess shouldBe true
    }

    it("should render a template with a compound identifier") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/compound-identifier-interpolation.beard")).mkString
      }

      val renderResult = StringWriterRenderResult()
      renderer.render(template, renderResult, Map("user" -> Map("name" -> "Gigi", "email" -> "gigi@gicu.com")))

      renderResult.result.toString should be("<div>gigi@gicu.com</div>")
    }

    describe("render a ForStatement") {
      it("should render a template with a for statement") {
        val template = BeardTemplateParser {
          Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-statement.beard")).mkString
        }

        val renderResult = StringWriterRenderResult()

        renderer.render(template, renderResult, Map("users" -> Seq(Map("name" -> "Gigi"))))
        renderResult.result.toString should be("<div>Hello</div>")
      }

      it("should render a template with a complex for statement") {
        val template = BeardTemplateParser {
          Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-complex-statement.beard")).mkString
        }

        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))))
        renderResult.result.toString should be("<div>Gigi</div><div>Gicu</div>")
      }

      it("should add the correct for related variables in the context") {
        val template = BeardTemplateParser {
          Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-context.beard")).mkString
        }

        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))))
        renderResult.result.toString should be("<div>isFirst:true-isLast:false-Gigi-isOdd:false-isEven:true</div>" +
          "<div>isFirst:false-isLast:true-Gicu-isOdd:true-isEven:false</div>")
      }
    }

    describe("render an IfStatement") {
      def context(cool: Boolean) =
        Map("user" -> Map("name" -> "Gigi", "isCool" -> cool))

      val template = templateCompiler.compile(TemplateName("/templates/if-statement.beard")).get

      describe("when the condition is true") {
        it("should render the template") {
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, context(true))
          renderResult.result.toString should be("\nGigi is cool\n")
        }
      }

      describe("when the condition is false") {
        it("should not render the template") {
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, context(false))
          renderResult.result.toString should be("")
        }
      }
    }

    describe("render an IfElseStatement") {
      def context(cool: Boolean) =
        Map("user" -> Map("name" -> "Gigi", "isCool" -> cool))

      val template = templateCompiler.compile(TemplateName("/templates/if-else-statement.beard")).get

      describe("when the condition is true") {
        it("should render the template") {
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, context(true))
          renderResult.result.toString should be("\nGigi is cool\n")
        }
      }

      describe("when the condition is false") {
        it("should not render the template") {
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, context(false))
          renderResult.result.toString should be("\nGigi is not cool\n")
        }
      }
    }

    describe("render an If Statement nested in a For Statement") {
      def context(cool: Boolean) =
        Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu")))

      val template = templateCompiler.compile(TemplateName("/templates/if-in-for-statement.beard")).get

      it("should render the template") {
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context(true))
        renderResult.result.toString should be("\nGigi is not odd\nGicu is odd\n")
      }
    }

    describe("nested If Statements") {
      def context(cool: Boolean) =
        Map("user" -> Map("name" -> "Gigi", "isCool" -> cool))

      val template = templateCompiler.compile(TemplateName("/templates/if-nested-statements.beard")).get

      it("should render the template") {
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context(true))
        renderResult.result.toString should be("""some
                                                 |  Gigi is cool
                                                 |    Gigi is still cool
                                                 |    some1
                                                 |some4""".stripMargin)
      }
    }

    describe("If Statements to check for non empty collections") {
      def context(cool: Boolean) =
        Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu")))

      val template = templateCompiler.compile(TemplateName("/templates/if-empty-collection.beard")).get

      describe("users collection is empty") {
        it("should render the template") {
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, Map("users" -> Seq()))
          renderResult.result.toString should be("""
                                                   |<div>No users</div>
                                                   |""".stripMargin)
        }
      }

      describe("users collection does not exist") {
        it("should not render the template") {
          val renderResult = StringWriterRenderResult()

          intercept[IllegalStateException] {
            renderer.render(template, renderResult, Map())
          }
        }
      }

      describe("users collection is present") {
        it("should render the template") {
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, context(true))
          renderResult.result.toString should be("""
                                                   |<ul>
                                                   |  <li>Gigi</li>
                                                   |  <li>Gicu</li>
                                                   |</ul>
                                                   |""".stripMargin)
        }
      }
    }

    it("should render a template with a render statement") {

      val r = templateCompiler.compile(TemplateName("/templates/layout-with-partial.beard"))
        .map { template =>
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, Map("example" -> Map("title" -> "Title", "presentations" ->
          Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
            Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2")))))
        renderResult.result.toString should not be ""
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
