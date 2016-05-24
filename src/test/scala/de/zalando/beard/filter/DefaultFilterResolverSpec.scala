package de.zalando.beard.filter

import org.scalatest.FunSpec
import org.slf4j.LoggerFactory

/**
 * @author dpersa
 */
class DefaultFilterResolverSpec extends FunSpec {

  val logger = LoggerFactory.getLogger(this.getClass)

  describe("DefaultFilterResolverTest") {

    it("should resolve") {
      logger.info("start")
      DefaultFilterResolver().resolve("currency", Set("symbol"))
    }
  }
}
