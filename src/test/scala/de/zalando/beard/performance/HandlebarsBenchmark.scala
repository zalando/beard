package de.zalando.beard.performance

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import org.scalameter.api._

import scala.collection.JavaConverters._

/**
 * @author dpersa
 */
object HandlebarsBenchmark extends Bench.LocalTime {

  val loader = new ClassPathTemplateLoader("/handlebars-benchmark")

  var handlebars = new Handlebars(loader)

  var template = handlebars.compile("index")

  val context = Map[String, AnyRef](
    "example" -> Map("title" -> "Mustache").asJava,
    "presentations" -> Seq(
      Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1").asJava,
      Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2").asJava).asJava).asJava

  val sizes = Gen.range("size")(1, 100000, 20000)
  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Handlebars" in {
    measure method "render" in {
      using(ranges) in {
        (r: Range) =>
          {
            r.foreach { _ =>
              template.apply(context)
            }
          }
      }
    }
  }

}
