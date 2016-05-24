package de.zalando.beard.filter

import org.scalatest.{FunSpec, Matchers}

/**
 * @author Emiliano Busiello.
 */
class UrlEncodeFilterSpec extends FunSpec with Matchers {

  describe("UrlEncodeFilterSpec") {

    val filter = new UrlEncodeFilter

    it("Should return the first letter/element") {
      filter.apply("Aaa+ ") should be("Aaa%2B%20")
      filter.applyIterable(List("+Aaa ", "Bbb")) should be(List("%2BAaa%20", "Bbb"))
      filter.applyMap(Map(1 -> "+Aaa ", 2 -> "Bbb")) should be(Map("1" -> "%2BAaa%20", "2" -> "Bbb"))
    }
  }
}