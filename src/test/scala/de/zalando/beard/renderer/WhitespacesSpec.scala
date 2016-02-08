package de.zalando.beard.renderer

import org.scalatest.{FunSpec, Matchers}
import org.slf4j.LoggerFactory

/**
  * @author dpersa
  */
class WhitespacesSpec extends FunSpec with Matchers {

  val logger = LoggerFactory.getLogger(this.getClass)

  val loader = new ClasspathTemplateLoader(
    templatePrefix = "/whitespaces/",
    templateSuffix = ".beard.html"
  )

  val templateCompiler = new CustomizableTemplateCompiler(templateLoader = loader)
  val renderer = new BeardTemplateRenderer (templateCompiler)

  val example1 = templateCompiler.compile(TemplateName("example1")).get
  val code = templateCompiler.compile(TemplateName("code")).get
  val forExample = templateCompiler.compile(TemplateName("for-example")).get
  val ifExample = templateCompiler.compile(TemplateName("if-example")).get
  val ifElseExample = templateCompiler.compile(TemplateName("if-else-example")).get
  val layout = templateCompiler.compile(TemplateName("layout")).get

  val verify = new Verify(renderer)

  describe("layout") {

    it("should render the correct file") {
      verify.whitespaces(layout, "layout")
    }
  }

  describe("example1") {

    it("should render the correct file") {
      verify.whitespaces(example1, "example1")
    }
  }

  describe("for-example") {

    it("should render the correct file") {
      verify.whitespaces(forExample, "for-example", Map("users" -> Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))))
    }
  }

  describe("if-example") {

    it("should render the correct file") {
      verify.whitespaces(ifExample, "if-true-example", Map("user" -> "Gigi", "condition" -> true))
      verify.whitespaces(ifExample, "if-false-example", Map("user" -> "Gigi", "condition" -> false))
    }
  }

  describe("if-else-example") {

    it("should render the correct file") {
      verify.whitespaces(ifElseExample, "if-else-true-example", Map("user" -> "Gigi", "condition" -> true))
      verify.whitespaces(ifElseExample, "if-else-false-example", Map("user" -> "Gigi", "condition" -> false))
    }
  }

  describe("code") {

    it("should render the correct file") {
      verify.whitespaces(code, "code", Map("imports" -> Seq(
        Map("name" -> "scala.collection.immutable.Map"),
        Map("name" -> "de.zalando.play.controllers.ArrayWrapper"))))
    }
  }
}
