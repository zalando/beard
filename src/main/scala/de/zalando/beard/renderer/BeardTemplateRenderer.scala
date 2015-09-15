package de.zalando.beard.renderer

import de.zalando.beard.ast._

import scala.collection.immutable._


/**
 * @author dpersa
 */
class BeardTemplateRenderer(templateCompiler: TemplateCompiler) {

  def render[T](template: BeardTemplate, result: RenderResult[T], context: Map[String, Any] = Map.empty): T = {
    renderInternal(template, context, result)
    result.complete()
    result.result
  }

  private def renderInternal[T](template: BeardTemplate,
                                context: Map[String, Any] = Map.empty,
                                renderResult: RenderResult[T]): Unit = {

    template.statements.map(renderStatement(_, context, renderResult))
  }

  private def onNext[T](renderResult: RenderResult[T], string: String) = {
    renderResult.write(string)
  }

  private def renderStatement[T](statement: Statement, context: Map[String, Any], renderResult: RenderResult[T]): Unit = {
    statement match {
      case Text(text) => onNext(renderResult, text)
      case IdInterpolation(identifier) => {
        onNext(renderResult, ContextResolver.resolve(identifier, context).toString())
      }
      case RenderStatement(template, localValues) =>
        val localContext = localValues.map {
          case attrWithId: AttributeWithIdentifier => attrWithId.key -> ContextResolver.resolve(attrWithId.id, context)
          case attrWitValue: AttributeWithValue => attrWitValue.key -> attrWitValue.value
        }.toMap
        renderInternal(templateCompiler.compile(TemplateName(template)).get, localContext, renderResult)
      case ForStatement(iterator, collection, statements) => {
        val seqFromContext: Seq[Any] = ContextResolver.resolveSeq(collection, context)

        for {
          map <- seqFromContext
          statement <- statements
        } yield {
          renderStatement(statement, context.updated(iterator.identifier, map), renderResult)
        }
      }
      case ExtendsStatement(template) => ()

      case _ => ()
    }
  }
}
