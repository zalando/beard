package de.zalando.beard.filter

import org.scalatest.{FunSpec, Matchers}

/**
 * @author Emiliano Busiello.
 */
class AbsFilterSpec extends FunSpec with Matchers {

  describe("AbsFilterTest") {

    val filter = new AbsFilter

    it("Should not modify positive values") {
      filter.apply("23") should be("23")
      filter.applyIterable(List(23, 25)) should be(List("23", "25"))
      filter.applyMap(Map(1 -> 23, 2 -> 25)) should be(Map("1" -> "23", "2" -> "25"))
    }

    it("Should modify negative values") {
      filter.apply("-23") should be("23")
      filter.applyIterable(List(-23, -25)) should be(List("23", "25"))
      filter.applyMap(Map(1 -> -23, 2 -> -25)) should be(Map("1" -> "23", "2" -> "25"))
    }
  }
}
