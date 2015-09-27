package de.zalando.beard.renderer

import de.zalando.beard.ast._
import de.zalando.beard.parser.BeardTemplateParser

import scala.annotation.tailrec
import scala.collection.GenTraversableOnce
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

class CustomizableTemplateCompiler(templateLoader: TemplateLoader = new ClasspathTemplateLoader(),
                                   templateCache: BeardTemplateCache = new BeardTemplateCache(),
                                   templateParser: BeardTemplateParser = new BeardTemplateParser())
  extends TemplateCompiler {

  def compile(templateName: TemplateName,
              yieldedStatements: Seq[Statement] = Seq.empty,
              contentForStatementsMap: Map[Identifier, Seq[Statement]] = Map.empty): Try[BeardTemplate] = {

    val beardTemplate = templateCache.get(templateName) match {
      case Some(template) => template
      case None =>
        val templateFileSource = templateLoader.load(templateName) match {
          case Some(content) => content
          case _ => throw new IllegalStateException(s"Could not find template with name ${templateName}")
        }

        val rawTemplate = templateParser.parse(templateFileSource.mkString)
        templateCache.add(templateName, rawTemplate)
        // TODO maybe do this in parallel
        compileRenderedTemplates(rawTemplate.renderStatements)
        rawTemplate
    }

    val newContentForStatementsMap = addContentForStatementsToMap(contentForStatementsMap,
      beardTemplate.contentForStatements)

    val mergedBeardTemplate = createMergedTemplate(beardTemplate, yieldedStatements, newContentForStatementsMap)

    mergedBeardTemplate.extended match {
      case Some(extendsStatement) =>
        val currentYieldedStatements = mergedBeardTemplate.statements
        compile(TemplateName(extendsStatement.template), currentYieldedStatements, newContentForStatementsMap)
      case None =>
        // we need to merge the texts and new lines
        val concatTextsTemplate = mergedBeardTemplate.copy(statements = concatTexts((mergedBeardTemplate.statements)))
        Success(concatTextsTemplate)
    }
  }

  private[renderer] def concatTextsSeq(existingTexts: Seq[HasText]): Seq[Text] =
    Seq(existingTexts.foldLeft(Text(""))((text, next) => Text(text.text + next.text)))


  /**
   * Given a sequence of Statements, some of them will be of type Text and some of them of type NewLine (instance of HasText)
   *
   * In case we have two consecutive Statements with text, we concat them into one text
   *
   * @param statements initial statements
   * @param mergedStatements here we put the result
   * @param existingTexts if we find a text, we stack it here until we concat them
   * @return
   */
  @tailrec
  private[renderer] final def concatTexts(statements: Seq[Statement],
                                          mergedStatements: Seq[Statement] = Seq.empty,
                                          existingTexts: Seq[HasText] = Seq.empty): Seq[Statement] = statements match {

    case Nil => mergedStatements ++ concatTextsSeq(existingTexts)
    case (head: HasText) :: tail => concatTexts(tail, mergedStatements, existingTexts :+ head)
    case (head: ForStatement) :: tail => {
      val concatTextsForStatement = head.copy(statements = concatTextsRec(head.statements))
      concatTexts(tail, mergedStatements ++ concatTextsSeq(existingTexts) :+ concatTextsForStatement, Seq.empty)
    }
    // TODO concat the texts for the other statements
    case head :: tail => concatTexts(tail, mergedStatements ++ concatTextsSeq(existingTexts) :+ head, Seq.empty)
  }

  private[renderer] def concatTextsRec(statements: Seq[Statement],
                                       mergedStatements: Seq[Statement] = Seq.empty,
                                       existingTexts: Seq[HasText] = Seq.empty): Seq[Statement] = {
    concatTexts(statements, mergedStatements, existingTexts)
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
      case YieldStatement() => if (yieldStatements.nonEmpty) yieldStatements else Seq(YieldStatement())
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
