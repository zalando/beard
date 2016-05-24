package de.zalando.beard.filter

import java.util.Locale

import org.scalatest.{FunSpec, Matchers}

/**
 * @author Emiliano Busiello.
 */
class LowercaseFilterSpec extends FunSpec with Matchers {
  describe("LowercaseFilterTest") {

    val filter = new LowercaseFilter

    it("Should not modify lowercase values") {
      filter.apply("aaa") should be ("aaa")
      filter.applyIterable(List("aaa", "bbb")) should be (List("aaa", "bbb"))
      filter.applyMap(Map(1 -> "aaa", 2 -> "bbb")) should be (Map("1" -> "aaa", "2" -> "bbb"))
    }

    it("Should modify non lowercase values") {
      filter.apply("AaA") should be ("aaa")
      filter.applyIterable(List("AaA", "bBB")) should be (List("aaa", "bbb"))
      filter.applyMap(Map(1 -> "AaA", 2 -> "bBB")) should be (Map("1" -> "aaa", "2" -> "bbb"))
    }
  }
}
