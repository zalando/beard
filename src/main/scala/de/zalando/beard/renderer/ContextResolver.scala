package de.zalando.beard.renderer

import de.zalando.beard.ast.CompoundIdentifier

/**
 * @author dpersa
 */
object ContextResolver {

  def resolveCollection(identifier: CompoundIdentifier, context: Map[String, Any]): Iterable[Any] = {

    context(identifier.identifierPart)

    val result = identifier.identifierParts.
      foldLeft(context(identifier.identifierPart)) { (ctx: Any, rest: String) =>
      ctx match {
        case map: Map[String, Any] if map.contains(rest) => map(rest)
        case map: Map[String, Any] => None
        case _ => throw new IllegalStateException(s"Can't resolve $identifier")
      }
    }

    result match {
      case it: Iterable[_] => it
      case op: Option[_] => op.toSeq
      case other =>
        throw new IllegalStateException(s"$identifier does not point to a Iterable or Option but a ${other.getClass} with value: $other")
    }
  }

  def resolve(identifier: CompoundIdentifier, context: Map[String, Any]): Option[Any] = {
    val result = identifier.identifierParts.
      foldLeft(context.get(identifier.identifierPart)) { (ctx: Option[Any], rest: String) =>
      ctx match {
        case Some(map: Map[String, Any]) if map.contains(rest) =>
          Option(map(rest))
        case _ => None
      }
    }
    result
  }
}
