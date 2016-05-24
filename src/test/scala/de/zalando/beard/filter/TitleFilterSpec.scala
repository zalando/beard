package de.zalando.beard.filter

import org.scalatest.{FunSpec, Matchers}

/**
 * @author Emiliano Busiello.
 */
class TitleFilterSpec extends FunSpec with Matchers {

  describe("TitleFilterTest") {

    val filter = new TitleFilter

    it("Should return the titled value") {
      filter.apply("aaa, lll") should be("Aaa, Lll")
      filter.applyIterable(List("aaa, lll", "bbb, lll")) should be(List("Aaa, Lll", "Bbb, Lll"))
      filter.applyMap(Map(1 -> "aaa, lll", 2 -> "bbb, lll")) should be(Map("1" -> "Aaa, Lll", "2" -> "Bbb, Lll"))
    }
  }
}
