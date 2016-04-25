package de.zalando.beard.filter

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Emiliano Busiello.
  */
class ReverseFilterSpec extends FunSpec with Matchers {
  describe("ReverseFilterTest") {

    val filter = new ReverseFilter
    it("Should return the reversed letter/element") {
      filter.apply("Aaa") should be("aaA")
      filter.applyIterable(List("Aaa", "Bbb")) should be(List("Bbb", "Aaa"))
      filter.applyMap(Map(1 -> "Aaa", 2 -> "Bbb")) should be(Map("2" -> "Bbb", "1" -> "Aaa"))
    }

    it("Should throw on empty collections") {
      intercept[IllegalArgumentException] {
        filter.applyIterable(List())
      }
      intercept[IllegalArgumentException] {
        filter.applyMap(Map())
      }
    }
  }
}
