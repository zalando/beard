package de.zalando.beard.renderer

import de.zalando.beard.ast._

import scala.Predef
import scala.collection.immutable._


/**
 * @author dpersa
 */
class BeardTemplateRenderer {


  def render(template: BeardTemplate, context: Map[String, Any]): String = {
    val result = StringBuilder.newBuilder

    template.parts.map(result ++= renderStatement(_, context))
    result.result()
  }

  private def renderStatement(statement: Statement, context: Map[String, Any]): String = {
    val result = StringBuilder.newBuilder
    statement match {
      case Text(text) => text
      case IdInterpolation(identifier) => {
        ContextResolver.resolve(identifier, context)
      }
      case ForStatement(iterator, collection, statements) => {

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
