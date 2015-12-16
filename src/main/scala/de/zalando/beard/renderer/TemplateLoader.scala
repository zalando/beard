package de.zalando.beard.renderer

import java.io.File

import scala.io.Source

/**
 * @author dpersa
 */
trait TemplateLoader {

  def load(templateName: TemplateName): Option[Source]
}

class ClasspathTemplateLoader(val templatePrefix: String = "",
                              val templateSuffix: String = "") extends TemplateLoader {

  override def load(templateName: TemplateName) = {

    val resource = Option(getClass.getResourceAsStream(s"${templatePrefix}${templateName.name}$templateSuffix"))

    resource.flatMap { res =>
      Option(Source.fromInputStream(res))
    }
  }
}

class FileTemplateLoader(val directoryPath: String,
                         val templateSuffix: String = ""
                         ) extends TemplateLoader {

  override def load(templateName: TemplateName) = {

    val file = new File(s"$directoryPath/${templateName.name}$templateSuffix")

    Option(Source.fromFile(file))
  }
}
