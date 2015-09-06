package de.zalando.beard.renderer

import de.zalando.beard.ast.{RenderStatement, YieldStatement, Statement, BeardTemplate}
import de.zalando.beard.parser.BeardTemplateParser

import scala.collection.immutable.Seq
import scala.util.{Success, Try}

/**
 * @author dpersa
 */
case class TemplateName(name: String) extends AnyVal

trait TemplateCompiler {
  def compile(templateName: TemplateName, yieldStatements: Seq[Statement] = Seq.empty): Try[BeardTemplate]
}

class CustomizableTemplateCompiler(val templateLoader: TemplateLoader,
                                   templateCache: BeardTemplateCache,
                                   templateParser: BeardTemplateParser) extends TemplateCompiler {
  def compile(templateName: TemplateName, yieldStatements: Seq[Statement] = Seq.empty): Try[BeardTemplate] = {
    templateCache.get(templateName) match {
      case Some(template) => Success(template)
      case None =>
        val templateFileSource = templateLoader.load(templateName) match {
          case Some(content) => content
          case _ => throw new IllegalStateException(s"Could not find template with name ${templateName}")
        }

        val beardTemplate = templateParser.parse(templateFileSource.mkString)

        compileRenderedTemplates(beardTemplate.renderStatements)

        val yieldedBeardTemplate = createYieldedTemplate(beardTemplate, yieldStatements)

        yieldedBeardTemplate.extended match {
          case Some(extendsStatement) =>
            val statements = yieldedBeardTemplate.statements
            compile(TemplateName(extendsStatement.template), statements)
          case None =>
            templateCache.add(templateName, yieldedBeardTemplate)
            Success(yieldedBeardTemplate)
        }
    }
  }

  /**
   * Compiles the templates that are rendered inside of a template
   * @param renderStatements the statements which render templates inside of an existing template
   */
  private def compileRenderedTemplates(renderStatements: Seq[RenderStatement]) = {
    for {
      statement <- renderStatements
    } yield compile(TemplateName(statement.template))
  }

  /**
   * Replaces the yield statements in the beard template with the ones specified as the argument
   * Replaces the render statements without parameters with the content of the template
   * @return a new BeardTemplate with the replaced statements
   */
  private def createYieldedTemplate(beardTemplate: BeardTemplate, yieldStatements: Seq[Statement]): BeardTemplate = {
    val newStatements: Seq[Statement] = beardTemplate.statements.flatMap {
      case YieldStatement() => yieldStatements
      // inline the render statements without parameters
      case renderStatement@RenderStatement(templateName, Seq()) =>
        templateCache.get(TemplateName(templateName)) match {
          case Some(renderedTemplate) => renderedTemplate.statements
          case None => Seq(renderStatement)
        }
      case statement => Seq(statement)
    }

    val yieldedBeardTemplate = BeardTemplate(newStatements, beardTemplate.extended, beardTemplate.renderStatements)
    yieldedBeardTemplate
  }
}

object DefaultTemplateCompiler extends CustomizableTemplateCompiler(new ClasspathTemplateLoader(),
  new BeardTemplateCache(),
  new BeardTemplateParser())
