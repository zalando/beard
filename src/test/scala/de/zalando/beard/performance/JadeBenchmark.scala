package de.zalando.beard.performance

import de.neuland.jade4j.{Jade4J, JadeConfiguration}
import de.neuland.jade4j.template.ClasspathTemplateLoader
import org.scalameter.api._

import scala.collection.JavaConverters._

/**
 * @author dpersa
 */
object JadeBenchmark extends Bench.LocalTime {

  val context = Map[String, AnyRef](
    "example" -> Map("title" -> "Mustache").asJava,
    "presentations" -> Seq(
      Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1").asJava,
      Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2").asJava).asJava).asJava

  val loader = new ClasspathTemplateLoader()
  val config = new JadeConfiguration()
  config.setTemplateLoader(loader)

  val template = config.getTemplate("jade-benchmark/index.jade")

  val sizes = Gen.range("size")(1, 100000, 20000)
  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Jade" in {
    measure method "render" in {
      using(ranges) in {
        (r: Range) =>
          {
            r.foreach { _ =>
              config.renderTemplate(template, context)
            }
          }
      }
    }
  }
}
