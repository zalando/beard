package de.zalando.beard.renderer

import java.util.Locale
import de.zalando.beard.parser.BeardTemplateParser
import org.scalatest.{FunSpec, Matchers}
import org.slf4j.LoggerFactory
import scala.collection.immutable.Seq
import scala.io.Source

/**
 * @author dpersa
 */
class BeardTemplateRendererSpec extends FunSpec with Matchers {

  val logger = LoggerFactory.getLogger(this.getClass)

  val templateCompiler = DefaultTemplateCompiler
  val renderer = new BeardTemplateRenderer(templateCompiler)

  describe("BeardTemplateRendererTest") {

    it("should render a template with a simple identifier") {
      templateCompiler.compile(TemplateName("/templates/identifier-interpolation.beard"))
        .map { template =>
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, Map("name" -> "Gigi"), None, EscapeStrategy.vanilla)
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
      val context = Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu")))

      it("should render a template with a for statement") {
        val template = BeardTemplateParser {
          Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-statement.beard")).mkString
        }

        val renderResult = StringWriterRenderResult()

        renderer.render(template, renderResult, context)
        renderResult.result.toString should be("<div>HelloGigi</div><div>HelloGicu</div>")
      }

      it("should render a template with a for statement with an index") {
        val template = BeardTemplateParser {
          Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-index-statement.beard")).mkString
        }

        val renderResult = StringWriterRenderResult()

        renderer.render(template, renderResult, context)
        renderResult.result.toString should be("<div>0-HelloGigi</div><div>1-HelloGicu</div>")
      }

      it("should render a template with a complex for statement") {
        val template = BeardTemplateParser {
          Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-complex-statement.beard")).mkString
        }

        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context)
        renderResult.result.toString should be("<div>Gigi</div><div>Gicu</div>")
      }

      it("should add the correct for related variables in the context") {
        val template = BeardTemplateParser {
          Source.fromInputStream(getClass.getResourceAsStream(s"/templates/for-context.beard")).mkString
        }

        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context)
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
        renderResult.result.toString should be("\nGigi is not odd\n\nGicu is odd\n")
      }
    }

    describe("render a nested If Statements") {
      def context(cool: Boolean) =
        Map("user" -> Map("name" -> "Gigi", "isCool" -> cool))

      val template = templateCompiler.compile(TemplateName("/templates/if-nested-statements.beard")).get

      it("should render the template") {
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context(true))
        logger.info(renderResult.result.toString)
        renderResult.result.toString should be("""some
                                                 |  Gigi is cool
                                                 |    Gigi is still cool
                                                 |  some1
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
        it("should render else branch in the template") {
          val renderResult = StringWriterRenderResult()
          renderer.render(template, renderResult, Map.empty)
          renderResult.result.toString should be("""
                                                   |<div>No users</div>
                                                   |""".stripMargin)
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

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/templates/layout-with-partial.rendered")).mkString

      val renderResult = StringWriterRenderResult()
      templateCompiler.compile(TemplateName("/templates/layout-with-partial.beard"))
        .map { template =>
          renderer.render(template, renderResult, Map("example" -> Map("title" -> "Title", "presentations" ->
            Seq(
              Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
              Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2")))))
        }

      logger.trace("Result")
      logger.trace(renderResult.result.toString)

      renderResult.result.toString should be(expected)
    }

    it("should render a template with an export statement") {
      pending
    }

    it("should render a block statement") {
      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/templates/block-statement.rendered")).mkString
      val renderResult = StringWriterRenderResult()

      val r = templateCompiler.compile(TemplateName("/templates/block-statement.beard"))
        .map { template =>
          renderer.render(template, renderResult, Map())
        }

      renderResult.result.toString should be(expected)
    }

    describe("render contents of contentFor statement replacing a block statement") {

      it("should render a list replacing contents from inherited template") {
        val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/templates/contentFor-statement.rendered")).mkString
        val renderResult = StringWriterRenderResult()

        val r = templateCompiler.compile(TemplateName("/templates/contentFor-statement.beard"))
          .map { template =>
            renderer.render(template, renderResult, Map())
          }

        renderResult.result.toString should be(expected)
      }
    }

    describe("render contents under yield when inheriting") {

      it("should render template contents after block statement") {
        val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/templates/yield-statement.rendered")).mkString
        val renderResult = StringWriterRenderResult()

        val r = templateCompiler.compile(TemplateName("/templates/yield-statement.beard"))
          .map { template =>
            renderer.render(template, renderResult, Map())
          }

        renderResult.result.toString should be(expected)

      }
    }

    describe("render with escaping strategies") {
      val context = Map("section" -> "<script>alert('attacked')</script>")
      val template = templateCompiler.compile(TemplateName("/templates/xss-safe.beard")).get

      it("should render the raw text with Vanilla Escape Strategy") {
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context, escapeStrategy = EscapeStrategy.vanilla)
        renderResult.result.toString should be("<div><script>alert('attacked')</script></div>")
      }

      it("should render the escaped text with the HTML Escape Strategy") {
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context, escapeStrategy = EscapeStrategy.html)
        renderResult.result.toString should be(s"<div>&lt;script&gt;alert('attacked')&lt;/script&gt;</div>")
      }
    }

    describe("render with filters") {
      val context = Map("name" -> "Gigi")
      val template = templateCompiler.compile(TemplateName("/templates/filters/single-filter-uppercase.beard")).get
      it("should render the filtered interpolation result") {
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context)
        renderResult.result.toString should be ("<div>GIGI</div>")
      }
    }

    describe("render with multiple filters") {
      val context = Map("name" -> "Gigi")
      val template = templateCompiler.compile(TemplateName("/templates/filters/multiple-filter-upper-lowercase.beard")).get
      it("should render the filtered interpolation result") {
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context)
        renderResult.result.toString should be ("<div>GIGI</div><div>gigi</div>")
      }
    }

    describe("render with date") {
      // date -> 02/03/2016
      val millis = 1454454000000l
      val isoString = "2016-02-03T00:00:00.00Z"
      val template = templateCompiler.compile(TemplateName("/templates/filters/date-filter.beard")).get
      it("should render the date for epoch milliseconds accordingly") {
        val context = Map("now" -> millis, "format" -> Map("string" -> "yyyy"))
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context)
        renderResult.result.toString should be ("<div>2016</div>")
      }
      it("should render the date for ISO formatted date accordingly") {
        val context = Map("now" -> isoString, "format" -> Map("string" -> "yyyy"))
        val renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, context)
        renderResult.result.toString should be ("<div>2016</div>")
      }
    }

    describe("render with translation") {
      val template = templateCompiler.compile(TemplateName("/templates/filters/translation.beard")).get
      it("should render the appropriate message from the resource bundles") {
        var renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, Map("messageKey" -> "example.title"),
          None,
          EscapeStrategy.vanilla,
          Locale.forLanguageTag("it"),
          "messages")
        renderResult.result.toString should be ("<div>Ciao</div>")

        renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, Map("messageKey" -> "example.title"),
          None,
          EscapeStrategy.vanilla,
          Locale.forLanguageTag("en"),
          "messages")
        renderResult.result.toString should be ("<div>Hello</div>")

        renderResult = StringWriterRenderResult()
        renderer.render(template, renderResult, Map("messageKey" -> "example.title"),
          None,
          EscapeStrategy.vanilla,
          Locale.forLanguageTag("de"),
          "messages")
        renderResult.result.toString should be ("<div>Hallo</div>")
      }
    }
  }
}
