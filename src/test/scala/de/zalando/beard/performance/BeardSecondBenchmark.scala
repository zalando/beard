package de.zalando.beard.performance

import java.util.{Locale, ResourceBundle}

import de.zalando.beard.parser.BeardTemplateParser
import de.zalando.beard.renderer._
import monifu.concurrent.Implicits.globalScheduler
import org.scalameter.api._

import scala.io.Source

/**
 * @author dpersa
 */
object BeardSecondBenchmark extends Bench.LocalTime {
  val compiler = DefaultTemplateCompiler
  val renderer = new BeardTemplateRenderer(compiler)

  val template = BeardTemplateParser {
    Source.fromInputStream(getClass.getResourceAsStream("/beard-second-benchmark/index.beard")).mkString
  }

  var compiledTemplate = compiler.compile(TemplateName("/beard-second-benchmark/index.beard")).get

  val context: Map[String, Map[String, Object]] = Map("example" -> Map("title" -> "Title", "presentations" ->
    Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
      Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2"))))

  val sizes = Gen.range("size")(1, 100000, 20000)
  val ranges = for {
    size <- sizes
  } yield 0 until size

  val renderResult = new MonifuRenderResult()

  val result = renderer.render(compiledTemplate, renderResult, context, None, EscapeStrategy.vanilla)
  result.foreach(print)

  performance of "Beard" in {
    measure method "render" in {
      using(ranges) in {
        (r: Range) => {
          r.foreach { _ =>
            renderer.render(compiledTemplate, MonifuRenderResult(), context, None, EscapeStrategy.vanilla)
          }
        }
      }
    }
  }
}