package de.zalando.beard.renderer

import de.zalando.beard.ast.CompoundIdentifier
import org.scalatest.{Matchers, FunSpec}
import scala.collection.immutable.Seq

/**
 * @author dpersa
 */
class ContextResolverSpec extends FunSpec with Matchers {

  val users = Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))
  val emails = Seq("gicu@yahoo.com", "gigi@yahoo.com")

  val context = Map(
    "objects" -> Map("users" -> users, "emails" -> emails),
    "emails" -> emails)

  describe("ContextResolver") {

    describe("resolveSeq") {
      it("should resolve a seq of maps from the context") {
        ContextResolver.resolveCollection(CompoundIdentifier("objects", Seq("users")), context) should be(users)
      }

      it("should resolve a simple seq from the context") {
        ContextResolver.resolveCollection(CompoundIdentifier("emails"), context) should be(emails)
      }

      it("should resolve a seq with compound identifier from the context") {
        ContextResolver.resolveCollection(CompoundIdentifier("objects", Seq("emails")), context) should be(emails)
      }
    }
  }
}
