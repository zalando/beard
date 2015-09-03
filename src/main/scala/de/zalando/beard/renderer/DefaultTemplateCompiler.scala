package de.zalando.beard.renderer

import de.zalando.beard.ast.{YieldStatement, Statement, BeardTemplate}
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
        val templateFileContent = templateLoader.load(templateName).mkString
        val beardTemplate = templateParser.parse(templateFileContent)

        val newStatements: Seq[Statement] = beardTemplate.statements.flatMap {
          case YieldStatement() => yieldStatements
          case statement => Seq(statement)
        }

        val yieldedBeardTemplate = BeardTemplate(newStatements, beardTemplate.extended)

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
}

object DefaultTemplateCompiler extends CustomizableTemplateCompiler(new ClasspathTemplateLoader(),
  new BeardTemplateCache(),
  new BeardTemplateParser())
