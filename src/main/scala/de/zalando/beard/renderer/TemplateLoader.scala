package de.zalando.beard.renderer

import scala.io.Source

/**
 * @author dpersa
 */
trait TemplateLoader {

  def load(templateName: TemplateName): Option[Source]
}

class ClasspathTemplateLoader extends TemplateLoader {

  override def load(templateName: TemplateName) = {

    val resource = Option(getClass.getResourceAsStream(templateName.name))

    resource.flatMap { res =>
        Option(Source.fromInputStream(res))
    }
  }
}
