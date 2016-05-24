package de.zalando.beard.filter

import java.util.Locale
import de.zalando.beard.renderer.{TemplateName, StringWriterRenderResult, BeardTemplateRenderer, DefaultTemplateCompiler}
import org.scalatest.{Matchers, FunSpec}
import scala.io.Source

/**
 * @author afurdylo
 */
class NumberFilterSpec extends FunSpec with Matchers {

  Locale.setDefault(Locale.US)

  describe("NumberFilterTest") {
    val filter = new NumberFilter
    it("should not throw on empy collections") {
      filter.applyIterable(List()) should be(List())
      filter.applyIterable(List()) should be(List())
      filter.applyMap(Map()) should be(Map())
    }

    it("should format integer") {
      filter.apply("23") should be("23")
      filter.applyIterable(List("23", "24", "25")) should be(List("23", "24", "25"))
      filter.applyIterable(List(23045, 23046, 24045)) should be(List("23,045", "23,046", "24,045"))
      filter.applyMap(Map("1" -> 23045, 2 -> 23046, 3 -> 24045)) should be(Map("1" -> "23,045", "2" -> "23,046", "3" -> "24,045"))
    }

    it("should format integer with minimum fraction") {
      filter.apply("23045", Map("format" -> "000000")) should be("023045")
      filter.applyIterable(List("23045", "23046", "24045"), Map("format" -> "000000")) should be(List("023045", "023046", "024045"))
      filter.applyIterable(List(23045, 23046, 24045), Map("format" -> "000000")) should be(List("023045", "023046", "024045"))
      filter.applyMap(Map("1" -> 23045, 2 -> 23046, 3 -> 24045), Map("format" -> "000000")) should be(Map("1" -> "023045", "2" -> "023046", "3" -> "024045"))
    }

    it("should format be immutable") {
      filter.apply("23") should be("23")
    }

    it("should format integer with minimum integer digits") {
      filter.apply("23", Map("format" -> "0,000")) should be("0,023")
      filter.applyIterable(List("23", "24", "25"), Map("format" -> "0,000")) should be(List("0,023", "0,024", "0,025"))
      filter.applyIterable(List(23, 24, 25), Map("format" -> "0,000")) should be(List("0,023", "0,024", "0,025"))
      filter.applyMap(Map("1" -> 23, 2 -> 24, 3 -> 25), Map("format" -> "0,000")) should be(Map("1" -> "0,023", "2" -> "0,024", "3" -> "0,025"))
    }

    it("should format double") {
      filter.apply("23045.67") should be("23,045.67")
      filter.applyIterable(List("23045.67", "23045.68", "22048.67")) should be(List("23,045.67", "23,045.68", "22,048.67"))
      filter.applyIterable(List(23045.67, 23045.68, 22048.67)) should be(List("23,045.67", "23,045.68", "22,048.67"))
      filter.applyMap(Map("1" -> 23045.67, 2 -> 23045.68, 3 -> 22048.67)) should be(Map("1" -> "23,045.67", "2" -> "23,045.68", "3" -> "22,048.67"))
    }

    it("should format double with maximum fraction") {
      filter.apply("23.046", Map("format" -> ".00")) should be("23.05")
      filter.applyIterable(List("23.045", "22.045", "22.010"), Map("format" -> ".00")) should be(List("23.05", "22.05", "22.01"))
      filter.applyIterable(List(23.045, 22.045, 22.010), Map("format" -> ".00")) should be(List("23.05", "22.05", "22.01"))
      filter.applyMap(Map("1" -> 23.045, 2 -> 22.045, 3 -> 22.010), Map("format" -> ".00")) should be(Map("1" -> "23.05", "2" -> "22.05", "3" -> "22.01"))
    }

    it("should format currency") {
      filter.apply("23045", Map("format" -> "#.00 ¤")) should be("23045.00 $")
      filter.applyIterable(List("23045", "22045", "22010"), Map("format" -> "#.00 ¤")) should be(List("23045.00 $", "22045.00 $", "22010.00 $"))
      filter.applyIterable(List(23045, 22045, 22010), Map("format" -> "#.00 ¤")) should be(List("23045.00 $", "22045.00 $", "22010.00 $"))
      filter.applyMap(Map("1" -> 23045, 2 -> 22045, 3 -> 22010), Map("format" -> "#.00 ¤")) should be(Map("1" -> "23045.00 $", "2" -> "22045.00 $", "3" -> "22010.00 $"))
    }

    it("should format currency with rounding") {
      filter.apply("23.46789", Map("format" -> "#.00 ¤")) should be("23.47 $")
      filter.applyIterable(List("23.46789", "22.045", "22.910"), Map("format" -> "#.00 ¤")) should be(List("23.47 $", "22.05 $", "22.91 $"))
      filter.applyIterable(List(23.46789, 22.045, 22.910), Map("format" -> "#.00 ¤")) should be(List("23.47 $", "22.05 $", "22.91 $"))
      filter.applyMap(Map("1" -> 23.46789, 2 -> 22.045, 3 -> 22.910), Map("format" -> "#.00 ¤")) should be(Map("1" -> "23.47 $", "2" -> "22.05 $", "3" -> "22.91 $"))
    }

    it("should format currency with symbol GBP") {
      filter.apply("23", Map("format" -> "#.00 GBP")) should be("23.00 GBP")
      filter.applyIterable(List("23.46789", "22.045", "22.910"), Map("format" -> "#.00 GBP")) should be(List("23.47 GBP", "22.05 GBP", "22.91 GBP"))
      filter.applyIterable(List(23.46789, 22.045, 22.910), Map("format" -> "#.00 GBP")) should be(List("23.47 GBP", "22.05 GBP", "22.91 GBP"))
      filter.applyMap(Map("1" -> 23.46789, 2 -> 22.045, 3 -> 22.910), Map("format" -> "#.00 GBP")) should be(Map("1" -> "23.47 GBP", "2" -> "22.05 GBP", "3" -> "22.91 GBP"))
    }

    it("should format currency with symbol €") {
      filter.apply("23", Map("format" -> "#.00 €")) should be("23.00 €")
      filter.applyIterable(List("23.46789", "22.045", "22.910"), Map("format" -> "#.00 €")) should be(List("23.47 €", "22.05 €", "22.91 €"))
      filter.applyIterable(List(23.46789, 22.045, 22.910), Map("format" -> "#.00 €")) should be(List("23.47 €", "22.05 €", "22.91 €"))
      filter.applyMap(Map("1" -> 23.46789, 2 -> 22.045, 3 -> 22.910), Map("format" -> "#.00 €")) should be(Map("1" -> "23.47 €", "2" -> "22.05 €", "3" -> "22.91 €"))
    }

    describe("NumberFilter template integration") {
      val templateCompiler = DefaultTemplateCompiler
      val renderer = new BeardTemplateRenderer(templateCompiler)

      it("should format a number in template") {
        val expected = Source.fromInputStream(getClass.getResourceAsStream("/filters/number-filter.rendered")).mkString
        val renderResult = StringWriterRenderResult()

        templateCompiler.compile(TemplateName("/filters/number-filter.beard"))
          .map { template =>
            renderer.render(template, renderResult, Map("transaction" -> Map("amount" -> 12.29)))
          }

        renderResult.result.toString should be(expected)
      }
    }

    describe("NumberFilter template iterable integration") {
      val templateCompiler = DefaultTemplateCompiler
      val renderer = new BeardTemplateRenderer(templateCompiler)

      it("should format number in template") {
        val expected = Source.fromInputStream(getClass.getResourceAsStream("/filters/number-filter-iterable.rendered")).mkString
        val renderResult = StringWriterRenderResult()

        templateCompiler.compile(TemplateName("/filters/number-filter.beard"))
          .map { template =>
            renderer.render(template, renderResult, Map("transaction" -> Map("amount" -> List(12.29, 15.6, 17.8))))
          }

        renderResult.result.toString should be(expected)
      }

    }

    describe("NumberFilter template map integration") {
      val templateCompiler = DefaultTemplateCompiler
      val renderer = new BeardTemplateRenderer(templateCompiler)

      it("should format number in template") {
        val expected = Source.fromInputStream(getClass.getResourceAsStream("/filters/number-filter-map.rendered")).mkString
        val renderResult = StringWriterRenderResult()

        templateCompiler.compile(TemplateName("/filters/number-filter.beard"))
          .map { template =>
            renderer.render(template, renderResult, Map("transaction" -> Map("amount" -> Map("1" -> 12.29, "2" -> 15.6, "3" -> 17.8))))
          }

        renderResult.result.toString should be(expected)
      }

    }
  }
}
