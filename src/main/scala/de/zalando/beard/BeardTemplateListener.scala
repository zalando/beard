package de.zalando.beard

import de.zalando.beard.BeardParser._
import de.zalando.beard.ast._
import scala.collection.JavaConversions._

class BeardTemplateListener extends BeardBaseListener {

  var result: BeardTemplate = BeardTemplate(List.empty)

  override def exitText(ctx: TextContext): Unit = ctx.result = Text(ctx.TEXT().getText)

  override def exitIdentifier(ctx: IdentifierContext): Unit = ctx.result = Identifier(ctx.TEXT().getText)

  override def exitInterpolation(ctx: InterpolationContext): Unit = ctx.result = Interpolation(ctx.identifier().result)

  override def exitSentence(ctx: SentenceContext): Unit = {
    val parts: List[Part] = List(Option(ctx.text()).toSeq.map(_.result),
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
