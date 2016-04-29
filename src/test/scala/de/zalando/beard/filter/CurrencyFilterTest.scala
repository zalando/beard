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
      filter.applyIterable(List("23045.67", "25545.67", "43045.37")) should be (List("$23,045.67", "$25,545.67", "$43,045.37"))
      filter.applyIterable(List(23045.67, 25545.67, 43045.37)) should be (List("$23,045.67", "$25,545.67", "$43,045.37"))
      filter.applyMap(Map(1 -> "23045.67", 2 -> "25545.67", 3 -> "43045.37")) should be (Map("1" -> "$23,045.67", "2" -> "$25,545.67", "3" -> "$43,045.37"))
    }
    it("Should format currency with different symbol") {
      filter.apply("23045.67", Map("symbol" -> "GBP")) should be ("GBP23,045.67")
      filter.applyIterable(List("23045.67", "25545.67", "43045.37"), Map("symbol" -> "GBP")) should be (List("GBP23,045.67", "GBP25,545.67", "GBP43,045.37"))
      filter.applyIterable(List(23045.67, 25545.67, 43045.37), Map("symbol" -> "GBP")) should be (List("GBP23,045.67", "GBP25,545.67", "GBP43,045.37"))
      filter.applyMap(Map(1 -> "23045.67", 2 -> "25545.67", 3 -> "43045.37"), Map("symbol" -> "GBP")) should be (Map("1" -> "GBP23,045.67", "2" -> "GBP25,545.67", "3" -> "GBP43,045.37"))
    }
    it("Should format currency custom") {
      filter.apply("23045", Map("format" -> "#.00 ¤")) should be ("23045.00 $")
      filter.applyIterable(List("23045.67", "25545.67", "43045.37"), Map("format" -> "#.00 ¤")) should be (List("23045.67 $", "25545.67 $", "43045.37 $"))
      filter.applyIterable(List(23045.67, 25545.67, 43045.37), Map("format" -> "#.00 ¤")) should be (List("23045.67 $", "25545.67 $", "43045.37 $"))
      filter.applyMap(Map(1 -> "23045.67", 2 -> "25545.67", 3 -> "43045.37"), Map("format" -> "#.00 ¤")) should be (Map("1" -> "23045.67 $", "2" -> "25545.67 $", "3" -> "43045.37 $"))
    }
    it("Should format currency with rounding") {
      filter.apply("23.46789", Map("format" -> "#.00 ¤")) should be ("23.47 $")
      filter.applyIterable(List("23.04567", "25.54567", "43.04537"), Map("format" -> "#.00 ¤")) should be (List("23.05 $", "25.55 $", "43.05 $"))
      filter.applyIterable(List(23.04567, 25.54567, 43.04537), Map("format" -> "#.00 ¤")) should be (List("23.05 $", "25.55 $", "43.05 $"))
      filter.applyMap(Map(1 -> "23.04567", 2 -> "25.54567", 3 -> "43.04537"), Map("format" -> "#.00 ¤")) should be (Map("1" -> "23.05 $", "2" -> "25.55 $", "3" -> "43.05 $"))
    }

    it("Should format currency with symbol GBP") {
      filter.apply("23", Map("format" -> "#.00 GBP")) should be ("23.00 GBP")
      filter.applyIterable(List("23", "25", "43"), Map("format" -> "#.00 GBP")) should be (List("23.00 GBP", "25.00 GBP", "43.00 GBP"))
      filter.applyIterable(List(23, 25, 43), Map("format" -> "#.00 GBP")) should be (List("23.00 GBP", "25.00 GBP", "43.00 GBP"))
      filter.applyMap(Map(1 -> "23", 2 -> "25", 3 -> "43"), Map("format" -> "#.00 GBP")) should be (Map("1" -> "23.00 GBP", "2" -> "25.00 GBP", "3" -> "43.00 GBP"))
    }
    it("Should format currency with symbol €") {
      filter.apply("23", Map("format" -> "#.00 €")) should be ("23.00 €")
      filter.applyIterable(List("23", "25", "43"), Map("format" -> "#.00 €")) should be (List("23.00 €", "25.00 €", "43.00 €"))
      filter.applyIterable(List(23, 25, 43), Map("format" -> "#.00 €")) should be (List("23.00 €", "25.00 €", "43.00 €"))
      filter.applyMap(Map(1 -> "23", 2 -> "25", 3 -> "43"), Map("format" -> "#.00 €")) should be (Map("1" -> "23.00 €", "2" -> "25.00 €", "3" -> "43.00 €"))
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

  describe("CurrencyFilterTest template iterable integration") {
    val templateCompiler = DefaultTemplateCompiler
    val renderer = new BeardTemplateRenderer(templateCompiler)

    it("Should format currency in template") {
      val expected = Source.fromInputStream(getClass.getResourceAsStream("/filters/currency-filter-iterable.rendered")).mkString
      val renderResult = StringWriterRenderResult()

      val r = templateCompiler.compile(TemplateName("/filters/currency-filter.beard"))
        .map { template =>
          renderer.render(template, renderResult, Map("transaction" -> Map("amount" -> List(12.26, 14.56, 29.90), "currency" -> "GBP")))
        }

      renderResult.result.toString should be(expected)
    }

  }

  describe("CurrencyFilterTest template map integration") {
    val templateCompiler = DefaultTemplateCompiler
    val renderer = new BeardTemplateRenderer(templateCompiler)

    it("Should format currency in template") {
      val expected = Source.fromInputStream(getClass.getResourceAsStream("/filters/currency-filter-map.rendered")).mkString
      val renderResult = StringWriterRenderResult()

      val r = templateCompiler.compile(TemplateName("/filters/currency-filter.beard"))
        .map { template =>
          renderer.render(template, renderResult, Map("transaction" -> Map("amount" -> Map(1 -> 12.26, 2 -> 14.56, 3 -> 29.90), "currency" -> "GBP")))
        }

      renderResult.result.toString should be(expected)
    }

  }
}
