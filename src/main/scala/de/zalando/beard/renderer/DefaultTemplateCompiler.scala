package de.zalando.beard.renderer

import de.zalando.beard.ast._
import de.zalando.beard.parser.BeardTemplateParser

import scala.collection.immutable.Seq
import scala.util.{Success, Try}

/**
 * @author dpersa
 */
case class TemplateName(name: String) extends AnyVal

trait TemplateCompiler {
  def compile(templateName: TemplateName,
              yieldStatements: Seq[Statement] = Seq.empty,
              contentForStatements: Map[Identifier, Seq[Statement]] = Map.empty): Try[BeardTemplate]
}

class CustomizableTemplateCompiler(val templateLoader: TemplateLoader,
                                   templateCache: BeardTemplateCache,
                                   templateParser: BeardTemplateParser) extends TemplateCompiler {

  def compile(templateName: TemplateName,
              yieldedStatements: Seq[Statement] = Seq.empty,
              contentForStatementsMap: Map[Identifier, Seq[Statement]] = Map.empty): Try[BeardTemplate] = {

    templateCache.get(templateName) match {
      case Some(template) => Success(template)
      case None =>
        val templateFileSource = templateLoader.load(templateName) match {
          case Some(content) => content
          case _ => throw new IllegalStateException(s"Could not find template with name ${templateName}")
        }

        val beardTemplate = templateParser.parse(templateFileSource.mkString)

        compileRenderedTemplates(beardTemplate.renderStatements)

        val newContentForStatementsMap = addContentForStatementsToMap(contentForStatementsMap,
          beardTemplate.contentForStatements)

        val mergedBeardTemplate = createMergedTemplate(beardTemplate, yieldedStatements, newContentForStatementsMap)

        mergedBeardTemplate.extended match {
          case Some(extendsStatement) =>
            val currentYieldedStatements = mergedBeardTemplate.statements
            compile(TemplateName(extendsStatement.template), currentYieldedStatements, newContentForStatementsMap)
          case None =>
            templateCache.add(templateName, mergedBeardTemplate)
            Success(mergedBeardTemplate)
        }
    }
  }

  private[renderer] def addContentForStatementsToMap(contentForStatementsMap: Map[Identifier, Seq[Statement]],
                                   newContentForStatements: Seq[ContentForStatement]): Map[Identifier, Seq[Statement]] = {
    newContentForStatements match {
      case Nil => {
        contentForStatementsMap
      }
      case head :: tail =>
        if (!contentForStatementsMap.contains(head.identifier)) {
          addContentForStatementsToMap(contentForStatementsMap + (head.identifier -> head.statements), tail)
        } else {
          addContentForStatementsToMap(contentForStatementsMap, tail)
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
   * Replaces blocks with the corresponding contentFor and in case the content for is missing, it replaces the block with it's statements
   * @return a new BeardTemplate with the replaced statements
   */
  private def createMergedTemplate(beardTemplate: BeardTemplate,
                                   yieldStatements: Seq[Statement],
                                   contentForStatementsMap: Map[Identifier, Seq[Statement]]): BeardTemplate = {
    val newStatements: Seq[Statement] = beardTemplate.statements.flatMap {
      case YieldStatement() => yieldStatements

      case BlockStatement(identifier, statements) =>
        if (contentForStatementsMap.contains(identifier))
          contentForStatementsMap(identifier)
        else
          statements

      // inline the render statements without parameters
      case renderStatement@RenderStatement(templateName, Seq()) =>
        templateCache.get(TemplateName(templateName)) match {
          case Some(renderedTemplate) => renderedTemplate.statements
          case None => Seq(renderStatement)
        }
      case statement => Seq(statement)
    }

    val yieldedBeardTemplate = BeardTemplate(newStatements,
      beardTemplate.extended,
      beardTemplate.renderStatements,
      beardTemplate.contentForStatements)
    yieldedBeardTemplate
  }
}

object DefaultTemplateCompiler extends CustomizableTemplateCompiler(new ClasspathTemplateLoader(),
  new BeardTemplateCache(),
  new BeardTemplateParser())
