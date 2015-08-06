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


  override def exitIfOnlyStatement(ctx: IfOnlyStatementContext): Unit = {
    val statements: Seq[Statement] = ctx.statement().map(st => st.result).toList
    ctx.result = IfStatement(statements)
  }

  override def exitIfElseStatement(ctx: IfElseStatementContext): Unit = {
    val ifStatements = ctx.ifStatements.map(st => st.result).toList
    val elseStatements = ctx.elseStatements.map(st => st.result).toList
    ctx.result = IfStatement(ifStatements, elseStatements)
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


  override def exitStatement(ctx: StatementContext): Unit = {
    val statements: List[Statement] = List(
      Option(ctx.structuredStatement()).toSeq.map(_.ifStatement().result),
      Option(ctx.text()).toSeq.map(_.result),
      Option(ctx.interpolation()).toSeq.map(_.result))
      .flatten
    ctx.result = statements.head
  }

  override def exitBeard(ctx: BeardContext): Unit = {
    val sentences: List[Statement] = ctx.statement().map(_.result).toList
    ctx.result = sentences
    result = BeardTemplate(sentences)
  }
}
