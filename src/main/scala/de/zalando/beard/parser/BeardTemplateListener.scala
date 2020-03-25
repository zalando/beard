package de.zalando.beard.parser

import de.zalando.beard.BeardParser._
import de.zalando.beard.BeardParserBaseListener
import de.zalando.beard.ast._
import scala.collection.immutable.Seq
import de.zalando.beard.parser.JDKCollectionConvertersCompat.Converters._

class BeardTemplateListener extends BeardParserBaseListener {

  var result: BeardTemplate = BeardTemplate(List.empty)

  override def exitText(ctx: TextContext) = {
    ctx.result = Text((Option(ctx.TEXT()).map(_.getText).toSeq ++ Option(ctx.CURLY_BRACKET()).map(_.getText).toSeq).head)
  }

  override def exitNewLine(ctx: NewLineContext) = ctx.result = NewLine(ctx.NL().size())

  override def exitLeadingSpace(ctx: LeadingSpaceContext) = ctx.result = NewLine(ctx.NL().size())

  override def exitWhite(ctx: WhiteContext) = ctx.result = White(ctx.WS().size())

  override def exitIdentifier(ctx: IdentifierContext) = {
    ctx.result = Identifier(ctx.IDENTIFIER().getText)
  }

  override def exitCompoundIdentifier(ctx: CompoundIdentifierContext) = {
    val identifiers = ctx.IDENTIFIER().asScala.map(id => id.getText).toList

    ctx.result = CompoundIdentifier(identifiers.head, identifiers.tail)
  }

  override def exitFilter(ctx: FilterContext) = {
    val parameters = ctx.attribute().asScala.toList.map(_.result)
    ctx.result = FilterNode(ctx.identifier().result, parameters)
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
    ctx.result = RenderStatement(ctx.attrValue().result, ctx.attribute().asScala.map(_.result).toList)

  override def exitBlockStatement(ctx: BlockStatementContext) =
    ctx.result = BlockStatement(ctx.blockInterpolation().identifier().result, ctx.statement().asScala.flatMap(_.result).toList)

  override def exitContentForStatement(ctx: ContentForStatementContext) =
    ctx.result = ContentForStatement(ctx.contentForInterpolation().identifier().result, ctx.statement().asScala.flatMap(_.result).toList)

  override def exitIfOnlyStatement(ctx: IfOnlyStatementContext) = {
    val startingSpaceStatements = ctx.startingSpace.asScala.toList.map(_.result)
    val ifSpaceStatements = ctx.ifSpace.asScala.toList.map(_.result)

    val condition = ctx.ifInterpolation().compoundIdentifier().result
    val statements: Seq[Statement] = ctx.statement().asScala.flatMap(st => st.result).toList
    ctx.result = startingSpaceStatements ++ Seq(IfStatement(condition, statements ++ ifSpaceStatements))
  }

  override def exitIfElseStatement(ctx: IfElseStatementContext) = {
    val startingSpaceStatements = ctx.startingSpace.asScala.toList.map(_.result)
    val ifSpaceStatements = ctx.ifSpace.asScala.toList.map(_.result)
    val elseSpaceStatements = ctx.elseSpace.asScala.toList.map(_.result)

    val condition = ctx.ifInterpolation().compoundIdentifier().result
    val ifStatements = ctx.ifStatements.asScala.flatMap(st => st.result).toList
    val elseStatements = ctx.elseStatements.asScala.flatMap(st => st.result).toList
    ctx.result = startingSpaceStatements ++ Seq(IfStatement(condition, ifStatements ++ ifSpaceStatements, elseStatements ++ elseSpaceStatements))
  }

  override def exitUnlessOnlyStatement(ctx: UnlessOnlyStatementContext) = {
    val startingSpaceStatements = ctx.startingSpace.asScala.toList.map(_.result)
    val unlessSpaceStatements = ctx.unlessSpace.asScala.toList.map(_.result)

    val condition = ctx.unlessInterpolation().compoundIdentifier().result
    val statements: Seq[Statement] = ctx.statement().asScala.flatMap(_.result).toList
    ctx.result = startingSpaceStatements ++ Seq(UnlessStatement(condition, statements ++ unlessSpaceStatements))
  }

  override def exitUnlessElseStatement(ctx: UnlessElseStatementContext) = {
    val startingSpaceStatements = ctx.startingSpace.asScala.toList.map(_.result)
    val unlessSpaceStatements = ctx.unlessSpace.asScala.toList.map(_.result)
    val elseSpaceStatements = ctx.elseSpace.asScala.toList.map(_.result)

    val condition = ctx.unlessInterpolation().compoundIdentifier().result
    val unlessStatements = ctx.unlessStatements.asScala.flatMap(_.result).toList
    val elseStatements = ctx.elseStatements.asScala.flatMap(_.result).toList
    ctx.result = startingSpaceStatements ++ Seq(UnlessStatement(condition, unlessStatements ++ unlessSpaceStatements, elseStatements ++ elseSpaceStatements))
  }

  override def exitForStatement(ctx: ForStatementContext) = {
    val statements: Seq[Statement] = ctx.statement().asScala.flatMap(st => st.result).toList

    val leadingSpaceStatements = Option(ctx.leadingSpace()).toSeq.map(_.result).toList

    ctx.result = leadingSpaceStatements ++
      List(ForStatement(ctx.forInterpolation().iter.asScala.head.result, ctx.forInterpolation().index.asScala.headOption.map(_.result),
        ctx.forInterpolation().coll.asScala.head.result, statements, leadingSpaceStatements.nonEmpty))
  }

  override def exitAttrInterpolation(ctx: AttrInterpolationContext) = {
    val attributes = ctx.attribute().asScala.map(a => a.result).toList

    ctx.result =
      AttrInterpolation(ctx.identifier().result, attributes)
  }

  override def exitIdInterpolation(ctx: IdInterpolationContext) = {
    val filters = ctx.filter().asScala.toList.map(_.result)
    ctx.result = IdInterpolation(ctx.compoundIdentifier().result, filters)
  }

  override def exitInterpolation(ctx: InterpolationContext) = {
    ctx.result = List(Option(ctx.attrInterpolation()).toSeq.map(_.result), Option(ctx.idInterpolation()).toSeq.map(_.result)).flatten.head
  }

  override def exitStatement(ctx: StatementContext) = {
    val statements: List[Statement] = List(
      Option(ctx.structuredStatement()).toSeq.flatMap { structuredStatement =>
        Option(structuredStatement.yieldStatement()).toSeq.map(_.result) ++
          Option(structuredStatement.ifStatement()).toSeq.flatMap(_.result) ++
          Option(structuredStatement.unlessStatement()).toSeq.flatMap(_.result) ++
          Option(structuredStatement.forStatement()).toSeq.flatMap(_.result) ++
          Option(structuredStatement.renderStatement()).toSeq.map(_.result) ++
          Option(structuredStatement.blockStatement()).toSeq.map(_.result)
      },
      Option(ctx.text()).toSeq.map(_.result),
      Option(ctx.newLine()).toSeq.map(_.result),
      Option(ctx.white()).toSeq.map(_.result),
      Option(ctx.interpolation()).toSeq.map(_.result))
      .flatten
    ctx.result = statements
  }

  override def exitBeard(ctx: BeardContext) = {
    val statements = ctx.statement().asScala.flatMap(_.result).toList
    val renderStatements = statements.collect {
      case renderStatement: RenderStatement => renderStatement
    }
    ctx.result = statements
    val extended = Option(ctx.extendsStatement()).map(_.result)
    val contentForStatements = ctx.contentForStatement().asScala.map(_.result).toList
    result = BeardTemplate(statements, extended, renderStatements, contentForStatements)
  }
}
