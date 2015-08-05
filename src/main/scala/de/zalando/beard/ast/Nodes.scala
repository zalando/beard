package de.zalando.beard.ast

import scala.collection.immutable.Seq

sealed trait Part

sealed trait Interpolation extends Part

case class AttrInterpolation(identifier: Identifier, attributes: Seq[Attribute] = Seq.empty) extends Interpolation {

  def attributeMap = attributes.map(attr => attr.key -> attr.value).toMap
}

case class IdInterpolation(identifier: Identifier, identifiers: Seq[Identifier] =  Seq.empty) extends Interpolation

case class Attribute(key: String, value: String)

case class Text(text: String) extends Part

case class Sentence(parts: List[Part]) extends Part

case class BeardTemplate(parts: List[Part])

case class Identifier(identifier: String)