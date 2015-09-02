package de.zalando.beard.performance

import java.io.StringWriter
import java.util

import de.zalando.beard.performance.Time._
import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

/**
 * @author dpersa
 */
class FreemarkerPerformanceTest extends FunSpec with Matchers {

  describe("freemarker") {
    it ("should render the freemarker template") {
      val config = new Configuration(Configuration.VERSION_2_3_23)
      val loader = new ClassTemplateLoader()

      config.setTemplateLoader(loader)

      val template = config.getTemplate("freemarker/index.ftl")

      val context = Map[String, AnyRef]("example" -> Map("title" -> "Freemarker").asJava, "presentations" -> Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1").asJava,
        Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2").asJava).asJava).asJava

      val con = new util.HashMap[String, Object]()
      con.putAll(context)

      val writer = new StringWriter()

      time("freemarker") {
        1 to REP foreach { i =>
          template.process(con, writer)
        }
      }

      //println(s"result: ${writer.toString()}")
    }
  }

}
