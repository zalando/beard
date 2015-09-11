package de.zalando.beard.renderer

import java.io.StringWriter

import de.zalando.beard.ast._
import rx.lang.scala.{Subject, Observable}
import rx.lang.scala.subjects.ReplaySubject

import scala.Predef
import scala.collection.immutable._


/**
 * @author dpersa
 */
class BeardTemplateRenderer(templateCompiler: TemplateCompiler) {

  def render(template: BeardTemplate, context: Map[String, Any] = Map.empty): StringWriter = {
    val output = new StringWriter()
    renderInternal(template, context, output)
    output
  }

  private def renderInternal(template: BeardTemplate,
                     context: Map[String, Any] = Map.empty,
                     output: StringWriter): Unit = {

    template.statements.map(renderStatement(_, context, output))
  }

  private def onNext(output: StringWriter, string: String) = {
    output.write(string)
  }

  private def renderStatement(statement: Statement, context: Map[String, Any], output: StringWriter): Unit = {
    statement match {
      case Text(text) => onNext(output, text)
      case IdInterpolation(identifier) => {
        onNext(output, ContextResolver.resolve(identifier, context).toString())
      }
      case RenderStatement(template, localValues) =>
        val localContext = localValues.map {
          case attrWithId: AttributeWithIdentifier => attrWithId.key -> ContextResolver.resolve(attrWithId.id, context)
          case attrWitValue: AttributeWithValue => attrWitValue.key -> attrWitValue.value
        }.toMap
        renderInternal(templateCompiler.compile(TemplateName(template)).get, localContext, output)
      case ForStatement(iterator, collection, statements) => {
        val seqFromContext: Seq[Any] = ContextResolver.resolveSeq(collection, context)

        for {
          map <- seqFromContext
          statement <- statements
        } yield {
          renderStatement(statement, context.updated(iterator.identifier, map), output)
        }
      }
      case ExtendsStatement(template) => ()

      case _ => ()
    }
  }
}
