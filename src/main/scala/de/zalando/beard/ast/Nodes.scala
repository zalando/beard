package de.zalando.beard.ast

import scala.collection.immutable.Seq

sealed trait Statement

sealed trait Interpolation extends Statement

case class AttrInterpolation(identifier: Identifier, attributes: Seq[Attribute] = Seq.empty) extends Interpolation {

  def attributeMap = attributes.map(attr => attr.key -> attr.value).toMap
}

case class IdInterpolation(identifier: Identifier, identifiers: Seq[Identifier] =  Seq.empty) extends Interpolation

case class IfStatement(ifStatements: Seq[Statement], elseStatements: Seq[Statement] = Seq.empty) extends Statement

case class Attribute(key: String, value: String)

case class Text(text: String) extends Statement

case class BeardTemplate(parts: Seq[Statement])

case class Identifier(identifier: String)