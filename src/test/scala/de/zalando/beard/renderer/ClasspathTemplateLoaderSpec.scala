package de.zalando.beard.renderer

import org.scalatest.{Matchers, FunSpec}

/**
 * @author dpersa
 */
class ClasspathTemplateLoaderSpec extends FunSpec with Matchers {

  describe("simple loader") {

    val loader = new ClasspathTemplateLoader()

    it("should load the template") {
      val template = loader.load(TemplateName("/loader/dir/template.beard"))
      template.isDefined.should(be(true))
    }

    describe("template doesn't exist") {
      it("should not load anything") {
        val template = loader.load(TemplateName("/does/not/exist"))
        template.isDefined.should(be(false))
      }
    }
  }

  describe("loader with prefix and suffix") {

    val loader = new ClasspathTemplateLoader(templatePrefix = "/loader/", templateSuffix = ".beard")

    it("should load the template") {
      val template = loader.load(TemplateName("dir/template"))
      template.isDefined.should(be(true))
    }
  }
}

