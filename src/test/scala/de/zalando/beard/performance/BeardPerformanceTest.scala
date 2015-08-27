package de.zalando.beard.performance

import de.zalando.beard.parser.BeardTemplateParser
import de.zalando.beard.performance.Time._
import de.zalando.beard.renderer.BeardTemplateRenderer
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

/**
 * @author dpersa
 */
class BeardPerformanceTest extends FunSpec with Matchers {

  describe("beard") {
    val partial = BeardTemplateParser {
      Source.fromInputStream(getClass.getResourceAsStream(s"/templates/_partial.beard")).mkString
    }

    val renderer = new BeardTemplateRenderer(Map("partial" -> partial))

    it ("should render the beard template") {
      val template = BeardTemplateParser {
        Source.fromInputStream(getClass.getResourceAsStream(s"/templates/layout-with-partial.beard")).mkString
      }

      val context: Map[String, Map[String, Object]] = Map("example" -> Map("title" -> "Title", "presentations" -> Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1"),
        Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2"))))

      var r = ""
      time {
        1 to REP foreach { i =>
          renderer.render(template, context)
        }
      }

      //println(s"result: ${r}")
    }
  }
}
