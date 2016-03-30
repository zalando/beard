package de.zalando.beard.filter

import de.zalando.beard.filter.implementations.DateFormatFilter
import de.zalando.beard.filter.implementations.TranslationFilter
import org.slf4j.LoggerFactory

import de.zalando.beard.ast.Identifier
import scala.collection.immutable.{Set, Seq, Map}

/**
  * @author dpersa
  */
trait FilterResolver {

  def registeredFilters = Seq(LowercaseFilter(), UppercaseFilter(), DateFormatFilter(), CapitalizeFilter(), TranslationFilter())

  def filters: Map[String, Filter]

  def resolve(identifier: String, parameterNames: Set[String]): Option[Filter]
}

case class DefaultFilterResolver(userFilters: Seq[Filter] = Seq()) extends FilterResolver {
  val logger = LoggerFactory.getLogger(this.getClass)
  
  override def resolve(identifier: String, parameterNames: Set[String]): Option[Filter] = {
    logger.info(s"Resolve filter ${identifier}")
    
    filters.get(identifier)
  }

  override def filters: Map[String, Filter] = {
    // User Filters overwrites registered filters map
    (registeredFilters ++ userFilters).map { case filter =>
      (filter.name, filter)
    }.toList.toMap[String, Filter]
  }
}
