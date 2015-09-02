package de.zalando.beard.renderer

import de.zalando.beard.ast._

import scala.Predef
import scala.collection.immutable._


/**
 * @author dpersa
 */
class BeardTemplateRenderer(templateCompiler: TemplateCompiler) {

  def render(template: BeardTemplate, context: Map[String, Any] = Map.empty): String = {
    val result = StringBuilder.newBuilder

    template.parts.map(result ++= renderStatement(_, context))
    result.result()
  }

  private def renderStatement(statement: Statement, context: Map[String, Any]): String = {
    statement match {
      case Text(text) => text
      case IdInterpolation(identifier) => {
        ContextResolver.resolve(identifier, context).toString()
      }
      case RenderStatement(template, localValues) =>
        val localContext = localValues.map {
          case attrWithId: AttributeWithIdentifier => attrWithId.key -> ContextResolver.resolve(attrWithId.id, context)
          case attrWitValue: AttributeWithValue => attrWitValue.key -> attrWitValue.value
        }.toMap
        render(templateCompiler.compile(TemplateName(template)).get, localContext)
      case ForStatement(iterator, collection, statements) => {
        val result = StringBuilder.newBuilder
        val seqFromContext: Seq[Any] = ContextResolver.resolveSeq(collection, context)

        seqFromContext.foreach { map =>
          result ++= statements.foldLeft("") { (result: String, s: Statement) =>
            result + renderStatement(s, context.updated(iterator.identifier, map))
          }
        }

        result.toString()
      }
      case _ => ""
    }
  }
}
