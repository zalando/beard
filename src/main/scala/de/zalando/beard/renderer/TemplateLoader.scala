package de.zalando.beard.renderer

import java.io.InputStream

import scala.io.Source

/**
 * @author dpersa
 */
trait TemplateLoader {

  def load(templateName: TemplateName): Source
}

class ClasspathTemplateLoader extends TemplateLoader {

  override def load(templateName: TemplateName) = {
    Source.fromInputStream(getClass.getResourceAsStream(templateName.name))
  }
}
