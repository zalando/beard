package de.zalando.beard.renderer

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, FunSpec}

import scala.io.Source

/**
  * @author dpersa
  */
class BugSpec extends FunSpec with Matchers with LazyLogging {

  val loader = new ClasspathTemplateLoader(
    templatePrefix = "/bug-examples/",
    templateSuffix = ".beard.html"
  )

  val templateCompiler = new CustomizableTemplateCompiler(templateLoader = loader)
  val renderer = new BeardTemplateRenderer(templateCompiler)



  describe("when there is a div in between comments") {

    it("should render the div") {
      val template = templateCompiler.compile(TemplateName("div-in-between-comments")).get

      val result = renderer.render(template, StringWriterRenderResult(), Map())

      val expected = Source.fromInputStream(getClass.getResourceAsStream(s"/bug-examples/div-in-between-comments.rendered")).mkString

      logger.info("Result: ")
      logger.info(result.toString)

      result.toString should be(expected)
    }
  }

}
