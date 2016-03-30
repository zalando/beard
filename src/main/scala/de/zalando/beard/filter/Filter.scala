package de.zalando.beard.filter

import java.text.{DecimalFormatSymbols, NumberFormat, DecimalFormat}
import java.time.format.DateTimeFormatter

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

class DateFormatFilter extends Filter {

  override def name = "date"

  override def apply(value: String, parameters: Map[String, Any]): String =
    parameters.get("format") match {
      case Some(format: String) => {
        val date = DateTimeFormatter.ISO_DATE.parse(value)
        DateTimeFormatter.ofPattern(format).format(date)
      }
      case Some(thing) => throw WrongParameterTypeException("format", "String")
      case None => throw ParameterMissingException("format")
    }
}

object DateFormatFilter {

  def apply(): DateFormatFilter = new DateFormatFilter()
}

class NumberFilter extends Filter {
  override def name: String = "number"

  override def apply(value: String, parameters: Predef.Map[String, Any]): String = {
    val number = BigDecimal(value)
    parameters.get("format") match {
      case Some(format: String) => {
        val formatter = new DecimalFormat(format)
        formatter.format(number)
      }
      case Some(thing) => throw WrongParameterTypeException("format", "String")
      case _ => NumberFormat.getNumberInstance.format(number)
    }
  }
}

object NumberFilter {
  def apply(): NumberFilter = new NumberFilter()
}

class CurrencyFilter extends Filter {
  override def name: String = "currency"

  override def apply(value: String, parameters: Predef.Map[String, Any]): String = {
    val number = BigDecimal(value)
    val currency = parameters.get("symbol") match {
      case Some(symbol: String) => {
        Option(symbol)
      }
      case Some(thing) => throw WrongParameterTypeException("symbol", "String")
      case _ => None
    }
    val formatter =  parameters.get("format") match {
      case Some(format: String) => {
        new DecimalFormat(format)
      }
      case Some(thing) => throw WrongParameterTypeException("format", "String")
      case _ => NumberFormat.getCurrencyInstance().asInstanceOf[DecimalFormat]
    }
    if (currency.isDefined) {
      val dfs = new DecimalFormatSymbols()
      dfs.setCurrencySymbol(currency.get)
      formatter.setDecimalFormatSymbols(dfs)
    }
    formatter.format(number)
  }
}

object CurrencyFilter {
  def apply(): CurrencyFilter = new CurrencyFilter()
}

class CapitalizeFilter extends Filter {
  override def name = "capitalize"

  override def apply(value: String, parameters: Map[String, Any]) : String =
    value.capitalize
}

object CapitalizeFilter {
  def apply(): CapitalizeFilter = new CapitalizeFilter()
}
