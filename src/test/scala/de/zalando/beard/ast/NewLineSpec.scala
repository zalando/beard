package de.zalando.beard.ast

import org.scalatest.{Matchers, FunSpec}

/**
 * @author dpersa
 */
class NewLineSpec extends FunSpec with Matchers {

  it("should return the right text") {
    NewLine(3).text should be("\n\n\n")
    NewLine(1).text should be("\n")
  }
}
