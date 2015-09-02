package de.zalando.beard.renderer

import de.zalando.beard.ast.BeardTemplate
import de.zalando.beard.parser.BeardTemplateParser

import scala.util.{Success, Try}

/**
 * @author dpersa
 */
case class TemplateName(name: String) extends AnyVal

trait TemplateCompiler {
  def compile(templateName: TemplateName): Try[BeardTemplate]
}

class CustomizableTemplateCompiler(val templateLoader: TemplateLoader,
                                   templateCache: BeardTemplateCache,
                                   templateParser: BeardTemplateParser) extends TemplateCompiler {
  def compile(templateName: TemplateName): Try[BeardTemplate] = {
    templateCache.get(templateName) match {
      case Some(template) => Success(template)
      case None =>
        val templateFileContent = templateLoader.load(templateName).mkString
        val beardTemplate = templateParser.parse(templateFileContent)
        templateCache.add(templateName, beardTemplate)
        Success(beardTemplate)
    }
  }
}

object DefaultTemplateCompiler extends CustomizableTemplateCompiler(new ClasspathTemplateLoader(),
  new BeardTemplateCache(),
  new BeardTemplateParser())
