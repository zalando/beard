package de.zalando.beard.filter

import org.scalatest.FunSpec
import org.slf4j.LoggerFactory
import org.scalatest._
import Matchers._

/**
  * @author boopathi
  */
class FilterManagerSpec extends FunSpec with Matchers {

  import FilterManager._

  val logger = LoggerFactory.getLogger(this.getClass())

  describe ("FilterManager suite") {

    describe ("Registry") {

      it ("should get the Filter") {
        rDummy.get("dummy") should be (Some(DummyFilter))
      }

      it ("should add new filter") {
        rDummy.add(DummyFilter2).get("dummy2") should be (Some(DummyFilter2))
        rDummy.get("dummy2") should be (None)
      }

    }

    describe ("register & registerOverwrite") {

      it ("should register new Filter into the passed registry and return a Monad") {

        val m = for {
          _ <- register(DummyFilter)
          _ <- register(DummyFilter2)
        } yield ()

        val state = m.exec(r)
        state.get("dummy") should be (Some(DummyFilter))
        state.get("dummy2") should be (Some(DummyFilter2))

      }

      it ("should throw when adding a new Filter with name which already exists in the registry") {

        val m = for {
          _ <- register(DummyFilter)
          _ <- register(DummyFilterClone)
        } yield ()

        intercept[FilterExists] {
          m.run(r)
        }

      }

      it ("should overwrite when register overwrite is called") {

        val m = for {
          _ <- register(DummyFilter)
          _ <- registerOverwrite(DummyFilterClone)
        } yield ()

        m.exec(r).get("dummy") should be (Some(DummyFilterClone))

      }

    }

    describe ("DefaultFilterRegistry") {

      it ("should contain a filters monad") {
        DefaultFilterRegistry.filters shouldBe a [StateRegistry[_]]
      }

    }

  }

  private val rDummy: Registry = Registry(Map[String, Filter](DummyFilter.name -> DummyFilter))

  private val r: Registry = Registry(Map.empty)

  private object DummyFilter extends Filter {

    def name: String = "dummy"

    def apply (value: String, parameters: Map[String, Any]): String =
      "dummy"

  }
  private object DummyFilter2 extends Filter {

    def name: String = "dummy2"

    def apply (value: String, parameters: Map[String, Any]): String =
      "dummy2"

  }

  private object DummyFilterClone extends Filter {

    def name: String = "dummy"

    def apply (value: String, parameters: Map[String, Any]): String =
      "dummy-clone"

  }

}
