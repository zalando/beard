package de.zalando.beard.renderer

import de.zalando.beard.ast._

import scala.collection.immutable._


/**
 * @author dpersa
 */
class BeardTemplateRenderer {


  def render(template: BeardTemplate, context: Map[String, String]): String = {
    val result = StringBuilder.newBuilder

    template.parts.map(result ++= renderStatement(_))
    result.result()
  }

  private def renderStatement(statement: Statement): String = {
    val result = StringBuilder.newBuilder
    statement match {
      case Text(text) => text
      case ForStatement(iterator, collection, statements) => {
        result ++= iterator.toString
        statements.foldLeft("") { (result: String, s: Statement) =>
          result + renderStatement(s)
        }
      }
      case _ => ""
    }
  }
}
