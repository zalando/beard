package de.zalando.beard.filter

import org.scalatest.{FunSpec, Matchers}

/**
  * @author boopathi
  */
class FilterSpec extends FunSpec with Matchers {

  describe("filters") {

    it("should capitalize") {
      val filter = getFilter("capitalize")
      filter.apply("capitalize") should be("Capitalize")
      filter.apply("cAPITALIZE") should be("CAPITALIZE")
    }

    it("should uppercase") {
      val filter = getFilter("uppercase")
      filter.apply("uppercase") should be("UPPERCASE")
      filter.apply("UpPeRcAse") should be("UPPERCASE")
    }

    it("should lowercase") {
      val filter = getFilter("lowercase")
      filter.apply("LoWeRcAse") should be("lowercase")
      filter.apply("LOWERCASE") should be("lowercase")
    }

    it("should convert date format") {
      val filter = getFilter("date")
      filter.apply("1991-03-10", Map("format" -> "MMddyy")) should be("031091")
      filter.apply("2011-01-01+01:00", Map("format" -> "D")) should be("1")
    }

  }

  private def getFilter(name: String) =
    DefaultFilterResolver().resolve(name, Set.empty) match {
      case Some(filter) => filter
      case None => throw FilterNotFound(name)
    }
}
