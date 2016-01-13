package de.zalando.beard.renderer

import org.scalatest.{Matchers, FunSpec}
import scala.collection.immutable.Seq

/**
  * @author dpersa
  */
class ForContextFactorySpec extends FunSpec with Matchers {

  describe("ForContextFactory") {

    it("should create") {
      val collectionOfContexts: Seq[Any] = Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))
      val globalContext = Map("users" -> collectionOfContexts)
      val forIterationContext = ForIterationContext(globalContext = globalContext,
        templateIteratorIdentifier = "user",
        collectionContext = Map("name" -> "Gigi"),
        currentIndex = 1, collectionOfContexts = collectionOfContexts)

      val context: Map[String, Any] = ForContextFactory.create(forIterationContext)

      context.should(be(Map("users" ->
        List(
          Map("name" -> "Gigi"),
          Map("name" -> "Gicu")),
        "user" ->
          Map("name" -> "Gigi",
            "isNotLast" -> false,
            "isFirst" -> false,
            "isLast" -> true,
            "isOdd" -> true,
            "isEven" -> false))))
    }
  }
}
