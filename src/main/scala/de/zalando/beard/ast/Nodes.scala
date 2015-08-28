package de.zalando.beard.ast

import scala.collection.immutable.Seq

sealed trait Statement

sealed trait Interpolation extends Statement

case class AttrInterpolation(identifier: Identifier, attributes: Seq[Attribute] = Seq.empty) extends Interpolation {

  def attributeMap = attributes.map(attr => attr.key -> attr.stringValue).toMap
}

case class IdInterpolation(identifier: CompoundIdentifier) extends Interpolation

case class ExtendsStatement(template: String) extends Statement

case class RenderStatement(template: String, localValues: Seq[Attribute] = Seq.empty) extends Statement

case class BlockStatement(identifier: Identifier, statements: Seq[Statement] = Seq.empty) extends Statement

case class IfStatement(ifStatements: Seq[Statement], elseStatements: Seq[Statement] = Seq.empty) extends Statement

case class ForStatement(iterator: Identifier, collection: CompoundIdentifier, statements: Seq[Statement] = Seq.empty) extends Statement

sealed trait Attribute {
  def key: String
  def stringValue: Option[String]
  def identifier: Option[CompoundIdentifier]
}

case class AttributeWithValue(key: String, value: String) extends Attribute {
  def identifier = None
  def stringValue = Some(value)
}

case class AttributeWithIdentifier(key: String, id: CompoundIdentifier) extends Attribute {
  def identifier = Some(id)
  def stringValue = None
}

case class Text(text: String) extends Statement

case class BeardTemplate(parts: Seq[Statement])

case class CompoundIdentifier(identifierPart: String, identifierParts: Seq[String] = Seq.empty)

case class Identifier(identifier: String)

