package de.zalando.beard.filter

import de.zalando.beard.ast.Identifier
import org.scalatest.FunSpec
import org.slf4j.LoggerFactory

/**
  * @author dpersa
  */
class DefaultFilterResolverTest extends FunSpec {

  val logger = LoggerFactory.getLogger(this.getClass)
  
  describe("DefaultFilterResolverTest") {

    it("should resolve") {
      logger.info("start")
      DefaultFilterResolver().resolve("currency", Set("symbol"))
    }

  }
}
