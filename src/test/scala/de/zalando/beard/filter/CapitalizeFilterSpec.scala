package de.zalando.beard.filter

import org.scalatest.{FunSpec, Matchers}

/**
 * @author Emiliano Busiello.
 */
class CapitalizeFilterSpec extends FunSpec with Matchers {
  describe("CapitalizeFilterTest") {

    val filter = new CapitalizeFilter

    it("Should not modify capitalized values") {
      filter.apply("Aaa") should be("Aaa")
      filter.applyIterable(List("Aaa", "Bbb")) should be(List("Aaa", "Bbb"))
      filter.applyMap(Map(1 -> "Aaa", 2 -> "Bbb")) should be(Map("1" -> "Aaa", "2" -> "Bbb"))
    }

    it("Should modify non capitalized values") {
      filter.apply("aaa") should be("Aaa")
      filter.applyIterable(List("aAa", "bbB")) should be(List("AAa", "BbB"))
      filter.applyMap(Map(1 -> "aaa", 2 -> "BBb")) should be(Map("1" -> "Aaa", "2" -> "BBb"))
    }
  }
}
