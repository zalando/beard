package de.zalando.beard.performance

import java.io.StringWriter
import java.util

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.extension.i18n.I18nExtension
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import org.scalameter.api._

import scala.jdk.CollectionConverters._

/**
 * @author dpersa
 */
object PebbleBenchmark extends Bench.LocalTime {
  val loader = new ClasspathLoader()
  loader.setSuffix(".html")
  loader.setPrefix("pebble-benchmark")

  val engine = new PebbleEngine.Builder().loader(loader).extension(new I18nExtension()).build()

  val template = engine.getTemplate("index")

  val context = Map[String, AnyRef]("presentations" ->
    Seq(
      Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1").asJava,
      Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2").asJava).asJava).asJava

  val con = new util.HashMap[String, Object]()
  con.putAll(context)

  val sizes = Gen.range("size")(1, 100000, 20000)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  val sr = new StringWriter()
  template.evaluate(sr, con)

  performance of "Pebble" in {
    measure method "render" in {
      using(ranges) in {
        (r: Range) =>
          {
            r.foreach { _ =>
              template.evaluate(new StringWriter(), con)
            }
          }
      }
    }
  }
}
