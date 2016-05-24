package de.zalando.beard.renderer

import java.io.File
import org.slf4j.LoggerFactory
import scala.io.Source

/**
 * @author dpersa
 */
trait TemplateLoader {

  def load(templateName: TemplateName): Option[Source]
}

class ClasspathTemplateLoader(
    val templatePrefix: String = "",
    val templateSuffix: String = "") extends TemplateLoader {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def load(templateName: TemplateName) = {

    val path = s"${templatePrefix}${templateName.name}$templateSuffix"

    val resource = Option(getClass.getResourceAsStream(path))

    logger.debug(s"Looking for template with path: $path")

    resource.flatMap { res =>
      Option(Source.fromInputStream(res))
    }
  }
}

class FileTemplateLoader(
    val directoryPath: String,
    val templateSuffix: String = ""
) extends TemplateLoader {

  override def load(templateName: TemplateName) = {

    val file = new File(s"$directoryPath/${templateName.name}$templateSuffix")

    Option(Source.fromFile(file))
  }
}
