package de.zalando.beard.ast

import scala.collection.immutable.Seq

sealed trait Statement

sealed trait Interpolation extends Statement

case class AttrInterpolation(identifier: Identifier, attributes: Seq[Attribute] = Seq.empty) extends Interpolation {

  def attributeMap = attributes.map(attr => attr.key -> attr.stringValue).toMap
}

case class IdInterpolation(
  identifier: CompoundIdentifier,
  filters: Seq[FilterNode] = Seq.empty)
    extends Interpolation

case class YieldStatement() extends Statement

case class ExtendsStatement(template: String) extends Statement

case class RenderStatement(template: String, localValues: Seq[Attribute] = Seq.empty) extends Statement

case class BlockStatement(identifier: Identifier, statements: Seq[Statement] = Seq.empty) extends Statement

case class ContentForStatement(identifier: Identifier, statements: Seq[Statement] = Seq.empty) extends Statement

case class IfStatement(condition: CompoundIdentifier, ifStatements: Seq[Statement], elseStatements: Seq[Statement] = Seq.empty) extends Statement

case class UnlessStatement(condition: CompoundIdentifier, unlessStatements: Seq[Statement], elseStatements: Seq[Statement] = Seq.empty) extends Statement

case class ForStatement(iterator: Identifier, index: Option[Identifier], collection: CompoundIdentifier,
  statements: Seq[Statement] = Seq.empty, addNewLine: Boolean = false) extends Statement

case class FilterNode(identifier: Identifier, parameters: Seq[Attribute] = Seq.empty)

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

trait HasText {
  def text: String
}

case class Text(text: String) extends Statement with HasText

case class NewLine(times: Int) extends Statement with HasText {
  override def text: String = (1 to times).foldLeft("")((s, time) => s + "\n")
}

case class White(times: Int) extends Statement with HasText {
  override def text: String = (1 to times).foldLeft("")((s, time) => s + " ")
}

case class BeardTemplate(
  statements: Seq[Statement],
  extended: Option[ExtendsStatement] = None,
  renderStatements: Seq[RenderStatement] = Seq.empty,
  contentForStatements: Seq[ContentForStatement] = Seq.empty)

object EmptyBeardTemplate extends BeardTemplate(Seq.empty)

case class CompoundIdentifier(identifierPart: String, identifierParts: Seq[String] = Seq.empty)

case class Identifier(identifier: String)

