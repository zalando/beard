package de.zalando.beard.renderer

/**
  * @author dpersa
  */
object ForContextFactory {

  def create(forIterationContext: ForIterationContext): Map[String, Any] = {
    // we verify if the collection is empty
//    forIterationContext.collectionContext match {
//      case
//    }

    // we add the context for the current iteration from the collection on which we iterate
    val newContext = forIterationContext.globalContext.updated(forIterationContext.templateIteratorIdentifier, forIterationContext.collectionContext)
    newContext(forIterationContext.templateIteratorIdentifier) match {
      case map: Map[String, Any] => {
        val index = forIterationContext.currentIndex
        val last = index == forIterationContext.collectionOfContexts.size - 1
        val newMap = map.
          updated("isLast", last).
          updated("isNotLast", !last).
          updated("isFirst", index == 0).
          updated("isOdd", index % 2 == 1).
          updated("isEven", index % 2 == 0)
        newContext.updated(forIterationContext.templateIteratorIdentifier, newMap)
      }
      case other =>
        throw new IllegalAccessException(s"We need a map here instead of ${other.getClass} with a value $other " +
          s"for iterator ${forIterationContext.templateIteratorIdentifier}:${forIterationContext.currentIndex}")
    }
  }
}

case class ForIterationContext(globalContext: Map[String, Any],
                               templateIteratorIdentifier: String,
                               collectionContext: Any,
                               currentIndex: Int,
                               collectionOfContexts: Iterable[Any])