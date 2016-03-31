package de.zalando.beard.filter

import java.util.Locale

import de.zalando.beard.renderer.{BeardTemplateRenderer, DefaultTemplateCompiler, TemplateName, StringWriterRenderResult}
import org.scalatest.{Matchers, FunSpec}

import scala.io.Source

/**
  * Created by afurdylo on 16/03/16.
  */
class CurrencyFilterTest extends FunSpec with Matchers {
  Locale.setDefault(Locale.US)
  describe("CurrencyFilterTest") {
    val filter = new CurrencyFilter
    it("Should format currency") {
      filter.apply("23045.67") should be ("$23,045.67")
    }
    it("Should format currency with different symbol") {
      filter.apply("23045.67", Map("symbol" -> "GBP")) should be ("GBP23,045.67")
    }
    it("Should format currency custom") {
      filter.apply("23045", Map("format" -> "#.00 ¤")) should be ("23045.00 $")
    }
    it("Should format currency with rounding") {
      filter.apply("23.46789", Map("format" -> "#.00 ¤")) should be ("23.47 $")
    }

    it("Should format currency with symbol GBP") {
      filter.apply("23", Map("format" -> "#.00 GBP")) should be ("23.00 GBP")
    }
    it("Should format currency with symbol €") {
      filter.apply("23", Map("format" -> "#.00 €")) should be ("23.00 €")
    }
  }

  describe("CurrencyFilterTest template integration") {
    val templateCompiler = DefaultTemplateCompiler
    val renderer = new BeardTemplateRenderer(templateCompiler)

    it("Should format currency in template") {
      val expected = Source.fromInputStream(getClass.getResourceAsStream("/filters/currency-filter.rendered")).mkString
      val renderResult = StringWriterRenderResult()

      val r = templateCompiler.compile(TemplateName("/filters/currency-filter.beard"))
        .map { template =>
          renderer.render(template, renderResult, Map("transaction" -> Map("amount" -> 12.26, "currency" -> "GBP")))
        }

      renderResult.result.toString should be(expected)
    }

  }
}
