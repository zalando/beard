package de.zalando.beard.renderer

import java.io.File

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
  val renderer = new BeardTemplateRenderer(templateCompiler)

  val example1 = templateCompiler.compile(TemplateName("example1")).get
  val forExample = templateCompiler.compile(TemplateName("for-example")).get
  val simpleFor = templateCompiler.compile(TemplateName("simple-for")).get
  val layout = templateCompiler.compile(TemplateName("layout")).get

  describe("layout") {

    it("should render the correct file") {
      val result = renderer.render(layout, StringWriterRenderResult(), Map())

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/layout.rendered")).mkString

      logger.info("Result: ")
      logger.info(result.toString)


      result.toString should be(expected)
    }
  }

  describe("example1") {

    it("should render the correct file") {
      val result = renderer.render(example1, StringWriterRenderResult(), Map())

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/example1.rendered")).mkString

      logger.info("Result: ")
      logger.info(result.toString)

      result.toString should be(expected)
    }
  }

  describe("for-example") {

    it("should render the correct file") {
      val result = renderer.render(forExample, StringWriterRenderResult(), Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))))

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/for-example.rendered")).mkString

      logger.info("Result: ")
      logger.info(result.toString)

      result.toString should be(expected)
    }
  }

  describe("simple-for") {

    it("should render the correct file") {
      val result = renderer.render(simpleFor, StringWriterRenderResult(), Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))))

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/whitespaces/simple-for.rendered")).mkString

      logger.info("Result: ")
      logger.info(result.toString)

      result.toString should be(expected)
    }
  }
}
