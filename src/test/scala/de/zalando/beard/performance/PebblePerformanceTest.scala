package de.zalando.beard.performance

import java.io.StringWriter
import java.util

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.extension.i18n.I18nExtension
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import de.zalando.beard.performance.Time._
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

/**
 * @author dpersa
 */
class
PebblePerformanceTest extends FunSpec with Matchers {

  describe("pebble") {

    it("should render the pebble template") {
      val loader = new ClasspathLoader()
      loader.setSuffix(".html")
      loader.setPrefix("pebble")

      val engine = new PebbleEngine(loader)
      engine.addExtension(new I18nExtension())

      val template = engine.getTemplate("index")

      val context = Map[String, AnyRef]("presentations" -> Seq(Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1").asJava,
        Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2").asJava).asJava).asJava

      val con = new util.HashMap[String, Object]()
      con.putAll(context)

      val writer = new StringWriter()

      time("pebble") {
        1 to REP foreach { i =>
          template.evaluate(writer, con)
        }
      }

      //println(s"result: ${writer.toString()}")
    }

  }
}
