package de.zalando.beard.renderer

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

/**
  * @author dpersa
  */
class WhitespacesSpec extends FunSpec with Matchers with LazyLogging {

  val loader = new ClasspathTemplateLoader(
    templatePrefix = "/whitespaces/",
    templateSuffix = ".beard.html"
  )

  val templateCompiler = new CustomizableTemplateCompiler(templateLoader = loader)
  val renderer = new BeardTemplateRenderer (templateCompiler)

  val example1 = templateCompiler.compile(TemplateName("example1")).get
  val code = templateCompiler.compile(TemplateName("code")).get
  val forExample = templateCompiler.compile(TemplateName("for-example")).get
  val layout = templateCompiler.compile(TemplateName("layout")).get

  describe("layout") {

    it("should render the correct file") {
      val result = renderer.render(layout, StringWriterRenderResult(), Map())

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/layout.rendered")).mkString

      logger.trace("Result: ")
      logger.trace(result.toString)

      result.toString should be(expected)
    }
  }

  describe("example1") {

    it("should render the correct file") {
      val result = renderer.render(example1, StringWriterRenderResult(), Map())

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/example1.rendered")).mkString

      logger.trace("Result: ")
      logger.trace(result.toString)

      result.toString should be(expected)
    }
  }

  describe("for-example") {

    it("should render the correct file") {
      val result = renderer.render(forExample, StringWriterRenderResult(), Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))))

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/for-example.rendered")).mkString

      logger.trace("Result: ")
      logger.trace(result.toString)

      result.toString should be(expected)
    }
  }

  describe("code") {

    it("should render the correct file") {
      val result = renderer.render(code, StringWriterRenderResult(), Map("imports" -> Seq(
        Map("name" -> "scala.collection.immutable.Map"),
        Map("name" -> "de.zalando.play.controllers.ArrayWrapper"))))

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/code.rendered")).mkString

      logger.trace("Result: ")
      logger.trace(result.toString)

      result.toString should be(expected)
    }
  }
}
