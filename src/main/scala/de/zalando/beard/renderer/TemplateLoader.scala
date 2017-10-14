package de.zalando.beard.renderer

import java.io.File
import org.slf4j.LoggerFactory
import scala.io.Source
import scala.util.{Try, Success, Failure}

/**
 * @author dpersa
 */
trait TemplateLoader {

  def load(templateName: TemplateName): Option[Source]

  def failure(templateName: TemplateName) = {
    new TemplateNotFoundException(s"Could not find template with name '${templateName.name}'")
  }
}

class ClasspathTemplateLoader(
    val templatePrefix: String = "",
    val templateSuffix: String = "") extends TemplateLoader {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def load(templateName: TemplateName) = {
    val path = buildPath(templateName)
    val resource = Option(getClass.getResourceAsStream(path))

    logger.debug(s"Looking for template with path: $path")

    resource.flatMap { res =>
      Option(Source.fromInputStream(res))
    }
  }

  override def failure(templateName: TemplateName) = {
    val path = buildPath(templateName)
    new TemplateNotFoundException(s"Expected to find template '${templateName.name}' in file '${path}', file not found on classpath")
  }

  def buildPath(templateName: TemplateName) =
    s"${templatePrefix}${templateName.name}$templateSuffix"
}

class FileTemplateLoader(
    val directoryPath: String,
    val templateSuffix: String = ""
) extends TemplateLoader {

  override def load(templateName: TemplateName) = {

    val path = buildPath(templateName)

    Try { Source.fromFile(path) } match {
      case Success(source) => Option(source)
      case Failure(_)      => None
    }
  }

  override def failure(templateName: TemplateName) = {
    val path = buildPath(templateName)
    new TemplateNotFoundException(s"Expected to find template '${templateName.name}' in file '${path}', file not found")
  }

  def buildPath(templateName: TemplateName) =
    s"$directoryPath/${templateName.name}$templateSuffix"
}

class TemplateNotFoundException(msg: String) extends Exception(msg) {}
