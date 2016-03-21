package de.zalando.beard.filter

import de.zalando.beard.renderer.{TemplateName, StringWriterRenderResult, BeardTemplateRenderer, DefaultTemplateCompiler}
import org.scalatest.{Matchers, FunSpec}

import scala.io.Source

/**
  * Created by afurdylo on 11/03/16.
  */
class NumberFilterTest extends FunSpec with Matchers {

  describe("NumberFilterTest") {
    val filter = new NumberFilter
    it("Should format integer") {
      filter.apply("23") should be("23")
    }
    it("Should format integer with minimum fraction") {
      filter.apply("23045", Map("format" -> "000000")) should be("023045")
    }
    it("Should format be immutable") {
      filter.apply("23") should be("23")
    }
    it("Should format integer with minimum integer digits") {
      filter.apply("23", Map("format" -> "0,000")) should be("0,023")
    }

    it("Should format double") {
      filter.apply("23045.67") should be("23,045.67")
    }
    it("Should format double with maximum fraction") {
      filter.apply("23.046", Map("format" -> ".00")) should be("23.05")
    }

    it("Should format money") {
      filter.apply("23045", Map("format" -> "#.00 ¤")) should be("23045.00 EUR")
    }
    it("Should format money with rounding") {
      filter.apply("23.46789", Map("format" -> "#.00 ¤")) should be("23.47 EUR")
    }

    it("Should format money with currency GBP") {
      filter.apply("23", Map("format" -> "#.00 GBP")) should be("23.00 GBP")
    }
    it("Should format money with currency €") {
      filter.apply("23", Map("format" -> "#.00 €")) should be("23.00 €")
    }

    describe("MoneyFilter template integration") {
      val templateCompiler = DefaultTemplateCompiler
      val renderer = new BeardTemplateRenderer(templateCompiler)

      it("Should format money in template") {
        val expected = Source.fromInputStream(getClass.getResourceAsStream("/filters/number-filter.rendered")).mkString
        val renderResult = StringWriterRenderResult()

        val r = templateCompiler.compile(TemplateName("/filters/number-filter.beard"))
          .map { template =>
            renderer.render(template, renderResult, Map("transaction" -> Map("amount" -> 12.26, "currency" -> "GBP")))
          }

        renderResult.result.toString should be(expected)
      }

    }

  }
}
