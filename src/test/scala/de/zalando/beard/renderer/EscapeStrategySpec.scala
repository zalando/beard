package de.zalando.beard.renderer

import org.scalatest.{Matchers, FunSpec}

/**
  * @autor cesarla
  */
class EscapeStrategySpec extends FunSpec with Matchers {
  describe("XMLEscapeStrategy") {
    it("should escape the XML/HTML special characters") {
      EscapeStrategy.xml.escape("<div>Test</div>") should be {
        s"&lt;div&gt;Test&lt;/div&gt;"
      }
    }
  }

  describe("VanillaEscapeStrategy") {
    it("should not escape any special character") {
      EscapeStrategy.vanilla.escape("<div>Test</div>") should be {
        s"<div>Test</div>"
      }
    }
  }
}
