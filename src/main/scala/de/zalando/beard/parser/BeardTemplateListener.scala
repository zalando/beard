package de.zalando.beard.parser

import de.zalando.beard.BeardParser._
import de.zalando.beard.BeardParserBaseListener
import de.zalando.beard.ast._
import scala.collection.immutable.Seq
import scala.collection.JavaConversions._

class BeardTemplateListener extends BeardParserBaseListener {

  var result: BeardTemplate = BeardTemplate(List.empty)

  override def exitText(ctx: TextContext) = ctx.result = Text(ctx.TEXT().getText)

  override def exitNewLine(ctx: NewLineContext) = ctx.result = NewLine(ctx.NL().size())

  override def exitWhite(ctx: WhiteContext) = ctx.result = White(ctx.WS().size())

  override def exitIdentifier(ctx: IdentifierContext) = {
    ctx.result = Identifier(ctx.IDENTIFIER().getText)
  }

  override def exitCompoundIdentifier(ctx: CompoundIdentifierContext) = {
    val identifiers = ctx.IDENTIFIER().map(id => id.getText).toList

    ctx.result = CompoundIdentifier(identifiers.head, identifiers.tail)
  }

  override def exitAttrValue(ctx: AttrValueContext) = ctx.result = ctx.ATTR_TEXT().getText

  override def exitAttribute(ctx: AttributeContext) = {
    val attributes = Option(ctx.attributeWithIdentifier()).map(_.result).toSeq ++ Option(ctx.attributeWithValue()).map(_.result).toSeq
    ctx.result = attributes.head
  }

  override def exitAttributeWithValue(ctx: AttributeWithValueContext) =
    ctx.result = AttributeWithValue(ctx.identifier.result.identifier, ctx.attrValue().ATTR_TEXT().getText)

  override def exitAttributeWithIdentifier(ctx: AttributeWithIdentifierContext) =
    ctx.result = AttributeWithIdentifier(ctx.identifier.result.identifier, ctx.compoundIdentifier().result)

  override def exitYieldStatement(ctx: YieldStatementContext) =
    ctx.result = YieldStatement()

  override def exitExtendsStatement(ctx: ExtendsStatementContext) =
    ctx.result = ExtendsStatement(ctx.attrValue().result)

  override def exitRenderStatement(ctx: RenderStatementContext) =
    ctx.result = RenderStatement(ctx.attrValue().result, ctx.attribute().map(_.result).toList)

  override def exitBlockStatement(ctx: BlockStatementContext) =
    ctx.result = BlockStatement(ctx.blockInterpolation().identifier().result, ctx.statement().map(_.result).toList)

  override def exitContentForStatement(ctx: ContentForStatementContext) =
    ctx.result = ContentForStatement(ctx.contentForInterpolation().identifier().result, ctx.statement().map(_.result).toList)

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
    val attributes = ctx.attribute().map(a => a.result).toList

    ctx.result =
      AttrInterpolation(ctx.identifier().result, attributes)
  }

  override def exitIdInterpolation(ctx: IdInterpolationContext) =
    ctx.result = IdInterpolation(ctx.compoundIdentifier().result)

  override def exitInterpolation(ctx: InterpolationContext) = {
    ctx.result = List(Option(ctx.attrInterpolation()).toSeq.map(_.result), Option(ctx.idInterpolation()).toSeq.map(_.result)).flatten.head
  }

  override def exitStatement(ctx: StatementContext) = {
    val statements: List[Statement] = List(
      Option(ctx.structuredStatement()).toSeq.flatMap { structuredStatement =>
        Option(structuredStatement.yieldStatement()).toSeq.map(_.result) ++
          Option(structuredStatement.ifStatement()).toSeq.map(_.result) ++
          Option(structuredStatement.forStatement()).toSeq.map(_.result) ++
          Option(structuredStatement.renderStatement()).toSeq.map(_.result) ++
          Option(structuredStatement.blockStatement()).toSeq.map(_.result)
      },
      Option(ctx.text()).toSeq.map(_.result),
      Option(ctx.newLine()).toSeq.map(_.result),
      Option(ctx.white()).toSeq.map(_.result),
      Option(ctx.interpolation()).toSeq.map(_.result))
      .flatten
    ctx.result = statements.head
  }

  override def exitBeard(ctx: BeardContext) = {
    val statements = ctx.statement().map(_.result).toList
    val renderStatements = statements.collect {
      case renderStatement: RenderStatement => renderStatement
    }
    ctx.result = statements
    val extended = Option(ctx.extendsStatement()).map(_.result)
    val contentForStatements = ctx.contentForStatement().map(_.result).toList
    result = BeardTemplate(statements, extended, renderStatements, contentForStatements)
  }
}
