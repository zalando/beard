package de.zalando.beard.renderer

import java.io.{File, FileNotFoundException}
import org.slf4j.LoggerFactory
import scala.io.Source
import scala.util.{Try, Success, Failure}

/**
 * @author dpersa
 */
trait TemplateLoader {

  def load(templateName: TemplateName): Try[String]
}

class ClasspathTemplateLoader(
    val templatePrefix: String = "",
    val templateSuffix: String = "") extends TemplateLoader {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def load(templateName: TemplateName) = {
    val path = s"${templatePrefix}${templateName.name}$templateSuffix"

    logger.debug(s"Looking for template with path: $path")

    val source = Option(getClass.getResourceAsStream(path))
      .flatMap(stream => Option(Source.fromInputStream(stream)))
      .flatMap(source => Option(source.mkString))
    source match {
      case Some(source) => new Success(source)
      case None         => new Failure(new TemplateLoadException(s"Expected to find template '${templateName.name}' in file '${path}', file not found on classpath"))
    }
  }
}

class FileTemplateLoader(
    val directoryPath: String,
    val templateSuffix: String = ""
) extends TemplateLoader {

  override def load(templateName: TemplateName) = {
    val path = s"$directoryPath/${templateName.name}$templateSuffix"
    Try {
      try {
        Source.fromFile(path).mkString
      } catch {
        case e: FileNotFoundException =>
          throw new TemplateLoadException(s"Expected to find template '${templateName.name}' in file '${path}', file not found")
      }
    }
  }
}

class TemplateLoadException(msg: String) extends Exception(msg) {}
