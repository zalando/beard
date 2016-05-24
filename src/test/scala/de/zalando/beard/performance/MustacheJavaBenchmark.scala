package de.zalando.beard.performance

import java.io.{PrintWriter, StringWriter}

import com.github.mustachejava.DefaultMustacheFactory
import org.scalameter.api._

import scala.collection.JavaConverters._

/**
 * @author dpersa
 */
object MustacheJavaBenchmark extends Bench.LocalTime {

  val context = Map[String, AnyRef](
    "example" -> Map("title" -> "Mustache").asJava,
    "presentations" -> Seq(
      Map("title" -> "Title1", "speakerName" -> "Name1", "summary" -> "Summary1").asJava,
      Map("title" -> "Title2", "speakerName" -> "Name2", "summary" -> "Summary2").asJava).asJava).asJava

  val mf = new DefaultMustacheFactory()
  val mustache = mf.compile("mustache-java-benchmark/application.mustache")

  val sizes = Gen.range("size")(1, 100000, 20000)
  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Mustache" in {
    measure method "render" in {
      using(ranges) in {
        (r: Range) =>
          {
            r.foreach { _ =>
              mustache.execute(new PrintWriter(new StringWriter()), context).flush()
            }
          }
      }
    }
  }
}
