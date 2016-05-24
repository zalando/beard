package de.zalando.beard.renderer

import de.zalando.beard.parser.BeardTemplateParser
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

class ChainingFiltersSpec extends FunSpec with Matchers {

  describe("when chaining filters") {

    val templateCompiler = DefaultTemplateCompiler
    val renderer = new BeardTemplateRenderer(templateCompiler)
    val template = BeardTemplateParser {
      Source.fromInputStream(getClass.getResourceAsStream(s"/templates/filters/chain-filters.beard")).mkString
    }

    describe("when the context has a string") {
      val context = Map("test" -> "hello world")

      it("should render the correct text") {
        val renderResult = StringWriterRenderResult()

        renderer.render(template, renderResult, context)

        renderResult.result.toString should be("dlroW olleH|Dlrow Olleh")
      }
    }

    describe("when the context has a seq") {
      val context = Map("test" -> Seq("hello world", "and others"))

      it("should render the correct text") {
        val renderResult = StringWriterRenderResult()

        renderer.render(template, renderResult, context)

        renderResult.result.toString should be("And Others,Hello World|And Others,Hello World")
      }
    }

    describe("when the context has a map") {
      val context = Map("test" -> Map("hello" -> "world", "and" -> "others"))

      it("should render the correct text") {
        val renderResult = StringWriterRenderResult()

        renderer.render(template, renderResult, context)

        renderResult.result.toString should be("(and,Others),(hello,World)|(and,Others),(hello,World)")
      }
    }
  }

}
