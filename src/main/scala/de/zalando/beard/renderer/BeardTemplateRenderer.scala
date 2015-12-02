package de.zalando.beard.renderer

import de.zalando.beard.ast._

import scala.Predef
import scala.collection.immutable._


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

  private def renderStatement[T](statement: Statement,
                                 context: Map[String, Any],
                                 renderResult: RenderResult[T],
                                 yieldedStatement: BeardTemplate): Unit = statement match {

    case Text(text) => onNext(renderResult, text)
    case IdInterpolation(identifier) => {
      onNext(renderResult, ContextResolver.resolve(identifier, context).toString())
    }
    case RenderStatement(template, localValues) =>
      val localContext = localValues.map {
        case attrWithId: AttributeWithIdentifier => attrWithId.key -> ContextResolver.resolve(attrWithId.id, context)
        case attrWitValue: AttributeWithValue => attrWitValue.key -> attrWitValue.value
      }.toMap
      renderInternal(templateCompiler.compile(TemplateName(template)).get, renderResult, localContext)
    case ForStatement(templateIterator, collection, statements) => {
      val collectionOfContexts: Seq[Any] = ContextResolver.resolveSeq(collection, context)

      for {
        index <- Range(0, collectionOfContexts.size)
        statement <- statements
      } yield {
        val currentIteratorContext = collectionOfContexts(index)
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

    case _ => ()
  }
}
