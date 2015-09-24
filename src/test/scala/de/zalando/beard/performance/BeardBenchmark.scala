package de.zalando.beard.performance

import de.zalando.beard.renderer._
import org.scalameter.api._

/**
 * @author dpersa
 */
object BeardBenchmark extends Bench.LocalTime {
  val compiler = DefaultTemplateCompiler
  val renderer = new BeardTemplateRenderer(compiler)
  val compiledTemplate = compiler.compile(TemplateName("/beard-benchmark/index.beard")).get

  val context: Map[String, Map[String, Object]] = Map("example" -> Map("title" -> "Title", "presentations" ->
    Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
      Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2"))))

  val sizes = Gen.range("size")(1, 100000, 20000)
  val ranges = for {
    size <- sizes
  } yield 0 until size

  val renderResult = StringWriterRenderResult()

  val result = renderer.render(compiledTemplate, renderResult, context)
  println(result)

  performance of "Beard" in {
    measure method "render" in {
      using(ranges) in {
        (r: Range) => {
          r.foreach { _ =>
            renderer.render(compiledTemplate, StringWriterRenderResult(), context)
          }
        }
      }
    }
  }
}