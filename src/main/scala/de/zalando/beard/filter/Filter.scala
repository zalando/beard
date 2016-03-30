package de.zalando.beard.filter

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, LocalDateTime, Instant}

import scala.collection.immutable.Map

/**
  * @author dpersa
  */
trait Filter {
  def name: String
  
  def apply(value: String, parameters: Map[String, Any] = Map.empty): String
}

class FilterException(message: String) extends RuntimeException(message)
case class ParameterMissingException(parameterName: String) extends FilterException(parameterName)
case class WrongParameterTypeException(parameterName: String, paramterType: String) extends FilterException(parameterName)
case class TypeNotSupportedException(filterName: String, className: String) extends FilterException(filterName)
case class FilterNotFound(filterName: String) extends FilterException(filterName)

case class InputFormatException(filterName: String, message: String) extends FilterException(message)

class LowercaseFilter extends Filter {

  override def name = "lowercase"

  override def apply(value: String, parameters: Map[String, Any]): String =
    value.toLowerCase
}

object LowercaseFilter {

  def apply(): LowercaseFilter = new LowercaseFilter()
}

class UppercaseFilter extends Filter {

  override def name = "uppercase"

  override def apply(value: String, parameters: Map[String, Any]): String =
    value.toUpperCase
}

object UppercaseFilter {

  def apply(): UppercaseFilter = new UppercaseFilter()
}



class CapitalizeFilter extends Filter {
  override def name = "capitalize"

  override def apply(value: String, parameters: Map[String, Any]) : String =
    value.capitalize
}

object CapitalizeFilter {
  def apply(): CapitalizeFilter = new CapitalizeFilter()
}
