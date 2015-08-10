package de.zalando.beard.parser

import de.zalando.beard.BeardParser._
import de.zalando.beard.BeardParserBaseListener
import de.zalando.beard.ast._
import scala.collection.immutable.Seq
import scala.collection.JavaConversions._

class BeardTemplateListener extends BeardParserBaseListener {

  var result: BeardTemplate = BeardTemplate(List.empty)

  override def exitText(ctx: TextContext) = ctx.result = Text(ctx.TEXT().getText)

  override def exitIdentifier(ctx: IdentifierContext) = {
    ctx.result = Identifier(ctx.IDENTIFIER().getText)
  }

  override def exitAttrValue(ctx: AttrValueContext) = ctx.result = ctx.ATTR_TEXT().getText

  override def exitAttribute(ctx: AttributeContext) = {
    ctx.result = (ctx.identifier.result.identifier, ctx.attrValue().ATTR_TEXT().getText)
  }

  override def exitIfOnlyStatement(ctx: IfOnlyStatementContext) = {
    val statements: Seq[Statement] = ctx.statement().map(st => st.result).toList
    ctx.result = IfStatement(statements)
  }

  override def exitIfElseStatement(ctx: IfElseStatementContext) = {
    val ifStatements = ctx.ifStatements.map(st => st.result).toList
    val elseStatements = ctx.elseStatements.map(st => st.result).toList
    ctx.result = IfStatement(ifStatements, elseStatements)
  }

  override def exitForStatement(ctx: ForStatementContext) = {
    val statements: Seq[Statement] = ctx.statement().map(st => st.result).toList
    ctx.result = ForStatement(ctx.forInterpolation().iter.head.result, ctx.forInterpolation().coll.head.result, statements)
  }

  override def exitAttrInterpolation(ctx: AttrInterpolationContext) = {
    val attributes = ctx.attribute().map(a => Attribute(a.result._1, a.result._2)).toList

    ctx.result =
      AttrInterpolation(ctx.identifier().result, attributes)
  }

  override def exitIdInterpolation(ctx: IdInterpolationContext) = {
    val identifiers = ctx.identifier().map(id => id.result).toList

    ctx.result = IdInterpolation(identifiers.head, identifiers.tail)
  }

  override def exitInterpolation(ctx: InterpolationContext) = {
    ctx.result = List(Option(ctx.attrInterpolation()).toSeq.map(_.result), Option(ctx.idInterpolation()).toSeq.map(_.result)).flatten.head
  }

  override def exitStatement(ctx: StatementContext) = {
    val statements: List[Statement] = List(
      Option(ctx.structuredStatement()).toSeq.flatMap { structuredStatement =>
        Option(structuredStatement.ifStatement()).toSeq.map(_.result) ++
          Option(structuredStatement.forStatement()).toSeq.map(_.result)
      },
      Option(ctx.text()).toSeq.map(_.result),
      Option(ctx.interpolation()).toSeq.map(_.result))
      .flatten
    ctx.result = statements.head
  }

  override def exitBeard(ctx: BeardContext) = {
    val sentences: List[Statement] = ctx.statement().map(_.result).toList
    ctx.result = sentences
    result = BeardTemplate(sentences)
  }
}
