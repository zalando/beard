package de.zalando.beard.renderer

import de.zalando.beard.ast._

import scala.annotation.tailrec

/**
  * @author dpersa
  */
class BeardTemplateRenderer(templateCompiler: TemplateCompiler) {

  def render[T](template: BeardTemplate,
                result: RenderResult[T],
                context: Map[String, Any] = Map.empty,
                layout: Option[BeardTemplate] = None): T = {
    layout match {
      case Some(layoutTemplate) =>
        renderInternal(layoutTemplate, result, context, template)
      case None =>
        renderInternal(template, result, context)
    }

    result.complete()
    result.result
  }

  private def renderInternal[T](template: BeardTemplate,
                                renderResult: RenderResult[T],
                                context: Predef.Map[String, Any] = Map.empty,
                                yieldedTemplate: BeardTemplate = EmptyBeardTemplate) = {

    template.statements.map(renderStatement(_, context, renderResult, yieldedTemplate))
  }

  private def onNext[T](renderResult: RenderResult[T], string: String) = {
    renderResult.write(string)
  }

  private def stringRepresentation(value: Any): String = value match {
    case null => ""
    case Some(str) => stringRepresentation(str)
    case None => ""
    case i: Iterable[_] => i map stringRepresentation mkString ","
    case other => other.toString
  }

  private def renderStatement[T](statement: Statement,
                                 context: Map[String, Any],
                                 renderResult: RenderResult[T],
                                 yieldedStatement: BeardTemplate): Unit = statement match {

    case Text(text) => onNext(renderResult, text)
    case IdInterpolation(identifier) => {
      val id = ContextResolver.resolve(identifier, context) match {
        case Some(value) => stringRepresentation(value)
        case _ => throw new IllegalStateException(s"The identifier ${identifier} was not resolved")
      }
      onNext(renderResult, id)
    }
    case RenderStatement(template, localValues) =>
      val localContext = localValues.map {
        case attrWithId: AttributeWithIdentifier => {
          // TOOD don't return an empty string
          attrWithId.key -> ContextResolver.resolve(attrWithId.id, context).getOrElse("")
        }
        case attrWitValue: AttributeWithValue => attrWitValue.key -> attrWitValue.value
      }.toMap
      renderInternal(templateCompiler.compile(TemplateName(template)).get, renderResult, localContext)
    case ForStatement(templateIterator, collection, statements) => {
      val collectionOfContexts = ContextResolver.resolveCollection(collection, context)

      for {
        (currentIteratorContext, index) <- collectionOfContexts.zipWithIndex
        statement <- statements
      } yield {
        val forIterationContext = ForIterationContext(globalContext = context,
          templateIteratorIdentifier = templateIterator.identifier,
          collectionContext = currentIteratorContext,
          currentIndex = index, collectionOfContexts = collectionOfContexts)

        renderStatement(statement,
          ForContextFactory.create(forIterationContext),
          renderResult,
          yieldedStatement)
      }
    }
    // extends should be ignored at render time
    case ExtendsStatement(template) => ()

    case YieldStatement() => renderInternal(yieldedStatement, renderResult, context)

    case IfStatement(condition, ifStatements, elseStatements) =>
      val result = ContextResolver.resolve(condition, context) match {
        case Some(result: Boolean)      => result
        case Some(result: Iterable[_])  => result.nonEmpty // includes Map as well
        case Some(result: Option[_])    => result.nonEmpty
        case Some(result: String)       => result.nonEmpty
        case Some(null)                 => false
        case Some(other) => throw new IllegalStateException(s"A condition should be of type Boolean or { def nonEmpty: Boolean } but it has ${other.getClass} type")
        case None => false
      }

      for (statement <- if (result) ifStatements else elseStatements) {
        renderStatement(statement, context, renderResult, yieldedStatement)
      }
    case _ => ()
  }
}
