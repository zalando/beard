package de.zalando.beard.filter

import org.scalatest.{FunSpec, Matchers}

/**
 * @author Emiliano Busiello.
 */
class UppercaseFilterSpec extends FunSpec with Matchers {
  describe("UppercaseFilterTest") {

    val filter = new UppercaseFilter
    it("Should not modify uppercase values") {
      filter.apply("AAA") should be ("AAA")
      filter.applyIterable(List("AAA", "BBB")) should be (List("AAA", "BBB"))
      filter.applyMap(Map(1 -> "AAA", 2 -> "BBB")) should be (Map("1" -> "AAA", "2" -> "BBB"))
    }
    it("Should modify non uppercase values") {
      filter.apply("aaa") should be ("AAA")
      filter.applyIterable(List("aaa", "bbb")) should be (List("AAA", "BBB"))
      filter.applyMap(Map(1 -> "aaa", 2 -> "bbb")) should be (Map("1" -> "AAA", "2" -> "BBB"))
    }
  }
}
