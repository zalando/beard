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
        templateIndexIdentifier = None,
        collectionContext = Map("name" -> "Gigi"),
        currentIndex = 1,
        collectionOfContexts = collectionOfContexts)

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

    describe("handleIndexContext") {
      val collectionOfContexts: Seq[Any] = Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))
      val globalContext = Map("users" -> collectionOfContexts)
      val templateIteratorIdentifier = "user"
      val collectionContext = Map("name" -> "Gigi")
      val currentIndex = 1
      val newContext = Map("user" -> Map("name" -> "Gigi"))

      it("should add the index to the context if templateIndexIdentifier is present") {
        val collectionOfContexts: Seq[Any] = Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))
        val globalContext = Map("users" -> collectionOfContexts)
        val forIterationContext = ForIterationContext(globalContext, templateIteratorIdentifier,
          Some("index"), collectionContext, currentIndex, collectionOfContexts)

        val context: Map[String, Any] = ForContextFactory.handleIndexContext(forIterationContext, newContext)

        context should be {
          Map(
            "user" -> Map("name" -> "Gigi"),
            "index" -> 1
          )
        }
      }

      it("should not add the index to the context if templateIndexIdentifier is not present") {
        val collectionOfContexts: Seq[Any] = Seq(Map("name" -> "Gigi"), Map("name" -> "Gicu"))
        val globalContext = Map("users" -> collectionOfContexts)
        val forIterationContext = ForIterationContext(globalContext, templateIteratorIdentifier,
          None, collectionContext, currentIndex, collectionOfContexts)

        val context: Map[String, Any] = ForContextFactory.handleIndexContext(forIterationContext, newContext)

        context should be {
          Map(
            "user" -> Map(
              "name" -> "Gigi"
            )
          )
        }
      }
    }
  }
}
