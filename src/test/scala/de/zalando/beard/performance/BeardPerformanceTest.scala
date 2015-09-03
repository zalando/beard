package de.zalando.beard.performance

import de.zalando.beard.parser.BeardTemplateParser
import de.zalando.beard.performance.Time._
import de.zalando.beard.renderer.{TemplateName, DefaultTemplateCompiler, BeardTemplateRenderer}
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

/**
 * @author dpersa
 */
class BeardPerformanceTest extends FunSpec with Matchers {

  describe("beard") {

    val compiler = DefaultTemplateCompiler
    val renderer = new BeardTemplateRenderer(compiler)

    it ("should render the beard template") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream("/templates/layout-with-partial.beard")).mkString
      }

      compiler.compile(TemplateName("/templates/layout-with-partial.beard"))
      compiler.compile(TemplateName("/templates/_footer.beard"))
      compiler.compile(TemplateName("/templates/_partial.beard"))

      val context: Map[String, Map[String, Object]] = Map("example" -> Map("title" -> "Title", "presentations" ->
        Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
        Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2"))))

      time("beard") {
        1 to REP foreach { i =>
          renderer.render(template, context)
        }
      }
//
//      val r = renderer.render(template, context)
//      r.subscribe(print(_))
//
//      println(s"result")
    }
  }
}
