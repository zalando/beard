package de.zalando.beard.performance

import java.io.StringWriter
import java.util

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import org.scalameter.api._

import scala.collection.JavaConverters._

/**
 * @author dpersa
 */
object FreemarkerBenchmark extends Bench.LocalTime {
  val config = new Configuration(Configuration.VERSION_2_3_23)
  val loader = new ClassTemplateLoader()

  config.setTemplateLoader(loader)

  val template = config.getTemplate("freemarker-benchmark/index.ftl")

  val context = Map[String, AnyRef](
    "example" -> Map("title" -> "Freemarker").asJava,
    "presentations" -> Seq(
      Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1").asJava,
      Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2").asJava).asJava).asJava

  val con = new util.HashMap[String, Object]()
  con.putAll(context)

  val sizes = Gen.range("size")(1, 100000, 20000)
  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Freemarker" in {
    measure method "render" in {
      using(ranges) in {
        (r: Range) =>
          {
            r.foreach { _ =>
              template.process(con, new StringWriter())
            }
          }
      }
    }
  }
}