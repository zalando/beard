package de.zalando.beard.renderer

import de.zalando.beard.ast.{Attribute, BeardTemplate, Identifier, Interpolation, Text}

import scala.collection.immutable._


/**
 * @author dpersa
 */
class BeardTemplateRenderer {


  def render(template: BeardTemplate, context: Map[String, String]): String = {
    val result = StringBuilder.newBuilder

    val tokens = template.parts.map {
      case Text(text)                                                            => result ++= text
//      case Interpolation(Identifier("if"), Seq(Attribute("cond", cond)))         =>
//      case Interpolation(Identifier(id), attrs) if attrs.isEmpty                 => result ++= context(id)

    }
    result.result()
  }

}
