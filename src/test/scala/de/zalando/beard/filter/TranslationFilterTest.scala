package de.zalando.beard.filter

import de.zalando.beard.filter.implementations.TranslationFilter
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by rweyand on 3/23/16.
  */
class TranslationFilterTest extends FunSpec with Matchers{
  describe("Translation filter") {
    it("should  resolve locale and bundle when given correct arguments") {
      val filter = TranslationFilter()
      filter.apply("example.title", Map("bundle" -> "messages", "locale" -> "de")) should be "Hallo"
      filter.apply("example.title", Map("bundle" -> "messages", "locale" -> "en")) should be "Hello"
      filter.apply("example.title", Map("bundle" -> "messages", "locale" -> "it")) should be "Ciao"
    }

    it("should complain about missing resource bundle") {
      val filter = TranslationFilter()
      a [ParameterMissingException] should be thrownBy filter.apply("example.title", Map("locale" -> "en"))
    }

    it("should complain about missing locale") {
      val filter = TranslationFilter()
      a [ParameterMissingException] should be thrownBy filter.apply("example.title", Map("bundle" -> "messages"))
    }
  }
}
