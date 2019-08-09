package de.zalando.beard.filter

import de.zalando.beard.filter.implementations.{DateFormatFilter, TranslationFilter}
import org.slf4j.LoggerFactory

import scala.collection.immutable.{Map, Seq, Set}

/**
 * @author dpersa
 */
trait FilterResolver {

  def registeredFilters = Seq(
    AbsFilter(),
    CapitalizeFilter(),
    CurrencyFilter(),
    DateFormatFilter(),
    FirstFilter(),
    LastFilter(),
    LowercaseFilter(),
    NumberFilter(),
    ReverseFilter(),
    TranslationFilter(),
    TitleFilter(),
    TrimFilter(),
    UppercaseFilter(),
    UrlEncodeFilter())

  def filters: Map[String, Filter]

  def resolve(identifier: String, parameterNames: Set[String]): Option[Filter]
}

case class DefaultFilterResolver(userFilters: Seq[Filter] = Seq()) extends FilterResolver {
  val logger = LoggerFactory.getLogger(this.getClass)

  override def resolve(identifier: String, parameterNames: Set[String]): Option[Filter] = {
    logger.debug(s"Resolve filter ${identifier}")

    filters.get(identifier)
  }

  override def filters: Map[String, Filter] = {
    // User Filters overwrites registered filters map
    (registeredFilters ++ userFilters).map {
      case filter =>
        (filter.name, filter)
    }.toList.toMap[String, Filter]
  }
}
