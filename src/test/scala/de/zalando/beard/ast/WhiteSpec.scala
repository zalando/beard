package de.zalando.beard.ast

import org.scalatest.{Matchers, FunSpec}

/**
 * @author rweyand
 */
class WhiteSpec extends FunSpec with Matchers {
  it("should return the right text") {
    White(3).text should be("   ")
    White(1).text should be(" ")
  }
}
