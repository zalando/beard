package de.zalando.beard.renderer

import java.util.Locale

import de.zalando.beard.ast._
import de.zalando.beard.filter.implementations.TranslationFilter
import de.zalando.beard.filter.{DefaultFilterResolver, Filter, FilterNotFound, FilterResolver}

import scala.collection.immutable.Seq

/**
  * @author dpersa
  */
class BeardTemplateRenderer(templateCompiler: TemplateCompiler,
                            filters: Seq[Filter] = Seq(),
                            filterResolver: FilterResolver = DefaultFilterResolver()) {

  def render[T](template: BeardTemplate,
                result: RenderResult[T],
                context: Map[String, Any] = Map.empty,
                layout: Option[BeardTemplate] = None,
                escapeStrategy: EscapeStrategy = EscapeStrategy.vanilla,
                locale: Locale = Locale.getDefault,
                resourceBundleName: String = ""
                ): T = {

    layout match {
      case Some(layoutTemplate) =>
        renderInternal(layoutTemplate, result, context, escapeStrategy, locale, resourceBundleName, template)
      case None =>
        renderInternal(template, result, context, escapeStrategy, locale, resourceBundleName)
    }

    result.complete()
    result.result
  }

  private def renderInternal[T](template: BeardTemplate,
                                renderResult: RenderResult[T],
                                context: Predef.Map[String, Any] = Map.empty,
                                escapeStrategy: EscapeStrategy,
                                locale: Locale,
                                resourceBundleName: String,
                                yieldedTemplate: BeardTemplate = EmptyBeardTemplate) = {

    template.statements.map(renderStatement(_, context, renderResult, yieldedTemplate, escapeStrategy, locale, resourceBundleName))
  }

  private def onNext[T](renderResult: RenderResult[T], string: String) = {
    renderResult.write(string)
  }

  private def stringRepresentation(value: Any, escapeStrategy: EscapeStrategy): String = value match {
    case s: String => escapeStrategy.escape(s) //shortcut the match if it's already a string.
    case null => ""
    case Some(str) => stringRepresentation(str, escapeStrategy)
    case None => ""
    case i: Iterable[_] => i map(e => stringRepresentation(e, escapeStrategy)) mkString ","
    case other => escapeStrategy.escape(other.toString)
  }

  private def renderStatement[T](statement: Statement,
                                 context: Map[String, Any],
                                 renderResult: RenderResult[T],
                                 yieldedStatement: BeardTemplate,
                                 escapeStrategy: EscapeStrategy,
                                 locale: Locale,
                                 resourceBundleName: String): Unit = statement match {

    case Text(text) => onNext(renderResult, text)
    case IdInterpolation(identifier, filters) => {
      val identifierValue = ContextResolver.resolve(identifier, context) match {
        case Some(value) => value
        case _ => throw new IllegalStateException(s"The identifier ${identifier} was not resolved")
      }

      val filteredIdentifierValue = filter(identifierValue, filters, context, locale, resourceBundleName)

      val filteredString = stringRepresentation(filteredIdentifierValue, escapeStrategy)

      onNext(renderResult, filteredString)
    }
    case RenderStatement(template, localValues) =>
      val localContext = localValues.map {
        case attrWithId: AttributeWithIdentifier => {
          // TOOD don't return an empty string
          attrWithId.key -> ContextResolver.resolve(attrWithId.id, context).getOrElse("")
        }
        case attrWitValue: AttributeWithValue => attrWitValue.key -> attrWitValue.value
      }.toMap
      renderInternal(templateCompiler.compile(TemplateName(template)).get, renderResult, localContext, escapeStrategy, locale, resourceBundleName)
    case ForStatement(templateIterator, templateIndex, collection, statements, addNewLine) => {
      val collectionOfContexts = ContextResolver.resolveCollection(collection, context)

      for {
        (currentIteratorContext, index) <- collectionOfContexts.zipWithIndex
        statement <- if (addNewLine) statements :+ Text("\n") else statements
      } yield {
        val forIterationContext = ForIterationContext(globalContext = context,
          templateIteratorIdentifier = templateIterator.identifier,
          templateIndexIdentifier = templateIndex match {
            case Some(index) => Option(index.identifier)
            case None => None
          },
          collectionContext = currentIteratorContext,
          currentIndex = index, collectionOfContexts = collectionOfContexts)

        renderStatement(statement,
          ForContextFactory.create(forIterationContext),
          renderResult,
          yieldedStatement,
          escapeStrategy,
          locale,
          resourceBundleName)
      }
    }
    // extends should be ignored at render time
    case ExtendsStatement(template) => ()

    case YieldStatement() => renderInternal(yieldedStatement, renderResult, context, escapeStrategy, locale, resourceBundleName)

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
        renderStatement(statement, context, renderResult, yieldedStatement, escapeStrategy, locale, resourceBundleName)
      }
    case _ => ()
  }

  /**
    * Apply a seq of filters to an identifier
    * If the filter list is empty the identifier value is return as default
    *
    * @param identifierValue: the actual value
    * @param filterNodes: a list of filters to be applied
    */
  private[this] def filter[T](identifierValue: Any,
                              filterNodes: Seq[FilterNode],
                              context: Map[String, Any],
                              locale: Locale,
                              resourceBundleName: String): Any = {

    filterNodes.foldLeft(identifierValue) {
      case (prevValue, filterNode) => {


        var parameters = filterNode.parameters.map {
          case AttributeWithIdentifier(key, id) => (key, ContextResolver.resolve(id, context))
          case AttributeWithValue(key, value) => (key, Option(value))
        }.filter(_._2.isDefined)
          .map{ case (k, v) => (k, v.get) }
          .toMap

        val filterIdentifier = filterNode.identifier.identifier
        val filter = DefaultFilterResolver(filters).resolve(filterIdentifier, Set.empty) match {
          case Some(filter: TranslationFilter) => {
            if(!(parameters.contains("bundle") && parameters.contains("locale"))) {
              parameters = parameters + ("bundle" -> resourceBundleName, "locale" -> locale)
            }
            filter
          }
          case Some(filter) => filter
          case None => throw FilterNotFound(filterIdentifier)
        }

        prevValue match { // check which filter needs to be applied.
          case m: Map[_, _] => filter.applyMap(m, parameters) // map's arity is different from the iterable.
          case i: Iterable[_] => filter.applyIterable(i, parameters)
          case other => filter.apply(other.toString, parameters)
        }
      }
    }
  }
}
