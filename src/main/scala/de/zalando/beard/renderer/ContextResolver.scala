package de.zalando.beard.renderer

import de.zalando.beard.ast.CompoundIdentifier

import scala.collection.immutable.{Seq, Map}

/**
 * @author dpersa
 */
object ContextResolver {

  def resolveSeq(identifier: CompoundIdentifier, context: Map[String, Any]): Seq[Any] = {

    context(identifier.identifierPart)

    val result = identifier.identifierParts.
      foldLeft(context(identifier.identifierPart)) { (ctx: Any, rest: String) =>
      ctx match {
        case map: Map[String, Any] => map(rest)
        case _ => throw new IllegalStateException(s"Can't resolve $identifier")
      }
    }

    result match {
      case seq: Seq[_] => seq
      case _ => throw new IllegalStateException(s"$identifier does not point to a Seq")
    }
  }

  def resolve(identifier: CompoundIdentifier, context: Map[String, Any]): String = {
    context(identifier.identifierPart)

    val result = identifier.identifierParts.
      foldLeft(context(identifier.identifierPart)) { (ctx: Any, rest: String) =>
      ctx match {
        case map: Map[String, Any] => map(rest)
        case _ => throw new IllegalStateException(s"Can't resolve $identifier")
      }
    }

    result.toString()
  }
}
