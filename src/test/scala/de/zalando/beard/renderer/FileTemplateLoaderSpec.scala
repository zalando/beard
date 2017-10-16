package de.zalando.beard.renderer

import org.scalatest.{Matchers, FunSpec}

/**
 * @author dpersa
 */
class FileTemplateLoaderSpec extends FunSpec with Matchers {

  describe("simple loader") {

    val loader = new FileTemplateLoader(".")

    it("should load the template") {
      val template = loader.load(TemplateName("src/test/resources/loader/dir/template.beard"))
      template.isSuccess.should(be(true))
    }

    describe("template doesn't exist") {
      it("should not load anything") {
        val template = loader.load(TemplateName("/does/not/exist"))
        template.isSuccess.should(be(false))
      }
    }
  }

  describe("loader with suffix") {

    val loader = new FileTemplateLoader(directoryPath = ".", templateSuffix = ".beard")

    it("should load the template") {
      val template = loader.load(TemplateName("src/test/resources/loader/dir/template"))
      template.isSuccess.should(be(true))
    }
  }
}

