package de.zalando.beard.filter

import org.scalatest.{FunSpec, Matchers}

/**
 * @author Emiliano Busiello.
 */
class FirstFilterSpec extends FunSpec with Matchers {
  describe("FirstFilterTest") {

    val filter = new FirstFilter
    it("Should return the first letter/element") {
      filter.apply("Aaa") should be("A")
      filter.applyIterable(List("Aaa", "Bbb")) should be(List("Aaa"))
      filter.applyMap(Map(1 -> "Aaa", 2 -> "Bbb")) should be(Map("1" -> "Aaa"))
    }

    it("Should throw on empty collections") {
      intercept[NoSuchElementException] {
        filter.apply("")
      }
      intercept[IllegalArgumentException] {
        filter.applyIterable(List())
      }
      intercept[IllegalArgumentException] {
        filter.applyMap(Map())
      }
    }
  }
}
