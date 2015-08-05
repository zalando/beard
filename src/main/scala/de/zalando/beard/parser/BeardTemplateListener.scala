package de.zalando.beard.parser

import de.zalando.beard.BeardParser._
import de.zalando.beard.BeardParserBaseListener
import de.zalando.beard.ast._
import scala.collection.immutable.Seq
import scala.collection.JavaConversions._

class BeardTemplateListener extends BeardParserBaseListener {

  var result: BeardTemplate = BeardTemplate(List.empty)

  override def exitText(ctx: TextContext): Unit = ctx.result = Text(ctx.TEXT().getText)

  override def exitIdentifier(ctx: IdentifierContext): Unit = {
    ctx.result = Identifier(ctx.IDENTIFIER().getText)
  }

  override def exitAttrValue(ctx: AttrValueContext): Unit = ctx.result = ctx.ATTR_TEXT().getText

  override def exitAttribute(ctx: AttributeContext): Unit = {
    ctx.result = (ctx.identifier.result.identifier, ctx.attrValue().ATTR_TEXT().getText)
  }

  override def exitIfBlock(ctx: IfBlockContext): Unit = {
    ctx.result = IfBlock(ctx.sentence().head.result, ctx.sentence().tail.lastOption.map(sentence => sentence.result))
  }

  override def exitAttrInterpolation(ctx: AttrInterpolationContext): Unit = {
    val attributes = ctx.attribute().map(a => Attribute(a.result._1, a.result._2)).toList

    ctx.result =
      AttrInterpolation(ctx.identifier().result, attributes)
  }

  override def exitIdInterpolation(ctx: IdInterpolationContext): Unit = {
    val identifiers = ctx.identifier().map(id => id.result).toList

    ctx.result = IdInterpolation(identifiers.head, identifiers.tail)
  }

  override def exitInterpolation(ctx: InterpolationContext): Unit = {
    ctx.result = List(Option(ctx.attrInterpolation()).toSeq.map(_.result), Option(ctx.idInterpolation()).toSeq.map(_.result)).flatten.head
  }

  override def exitSentence(ctx: SentenceContext): Unit = {
    val parts: List[Part] = List(
      Option(ctx.ifBlock()).toSeq.map(_.result),
      Option(ctx.text()).toSeq.map(_.result),
      Option(ctx.interpolation()).toSeq.map(_.result),
      Option(ctx.sentence()).toSeq.map(_.result))
      .flatten
    ctx.result = Sentence(parts)
  }

  override def exitBeard(ctx: BeardContext): Unit = {
    val sentences: List[Sentence] = ctx.sentence().map(_.result).toList
    ctx.result = sentences
    val flatten: List[Part] = flattenSentences(sentences)
    result = BeardTemplate(flatten)
  }

  def flattenSentences(parts: List[Part]): List[Part] = parts match {
    case Nil => Nil
    case Sentence(parts) :: tail => flattenSentences(parts) ++ flattenSentences(tail)
    case part :: tail => part :: flattenSentences(tail)
  }
}
