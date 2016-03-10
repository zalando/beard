package de.zalando.beard.filter

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

import scala.collection.immutable.Map

/**
  * @author dpersa
  */
trait Filter {

  def name: String
  
  def apply(value: String, parameters: Map[String, String] = Map.empty): String
}

class FilterException extends RuntimeException
case class ParameterMissingException(parameterName: String) extends FilterException
case class TypeNotSupportedException(filterName: String, className: String) extends FilterException
case class FilterNotFound(filterName: String) extends FilterException

class LowercaseFilter extends Filter {

  override def name = "lowercase"

  override def apply(value: String, parameters: Map[String, String]): String =
    value.toLowerCase
}

object LowercaseFilter {

  def apply(): LowercaseFilter = new LowercaseFilter()
}

class UppercaseFilter extends Filter {

  override def name = "uppercase"

  override def apply(value: String, parameters: Map[String, String]): String =
    value.toLowerCase
}

object UppercaseFilter {

  def apply(): UppercaseFilter = new UppercaseFilter()
}

class DateFormatFilter extends Filter {

  override def name = "date"

  override def apply(value: String, parameters: Map[String, String]): String =
    parameters.get("format") match {
      case Some(format) => {
        val date = DateTimeFormatter.ISO_DATE.parse(value)
        DateTimeFormatter.ofPattern(format).format(date)
      }
      case None => throw ParameterMissingException("format")
    }
}

object DateFormatFilter {

  def apply(): DateFormatFilter = new DateFormatFilter()
}

