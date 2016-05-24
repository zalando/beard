package de.zalando.beard.filter

import java.net.URLEncoder
import java.text.{DecimalFormat, DecimalFormatSymbols, NumberFormat}

import scala.annotation.tailrec
import scala.collection.immutable.Map

/**
 * @author dpersa
 */
trait Filter {
  def name: String

  def apply(value: String, parameters: Map[String, Any] = Map()): String

  def applyIterable(value: Iterable[_], parameters: Map[String, Any] = Map()): Iterable[String] =
    value.map(v => apply(v.toString, parameters))

  /**
   * Although a map is just an Iterable[(A, B)] it's hard to match on this type
   * because of how tuple are defined in scala, Map[String, String] is not a subtype of Iterable[String].
   */
  def applyMap(value: Map[_, _], parameters: Map[String, Any] = Map()): Map[String, String] =
    value.map{ case (k, v) => (k.toString, apply(v.toString, parameters)) }
}

class FilterException(message: String) extends RuntimeException(message)
case class ParameterMissingException(parameterName: String) extends FilterException(parameterName)
case class WrongParameterTypeException(parameterName: String, paramterType: String) extends FilterException(parameterName)
case class TypeNotSupportedException(filterName: String, className: String) extends FilterException(filterName)
case class FilterNotFound(filterName: String) extends FilterException(filterName)

case class InputFormatException(filterName: String, message: String) extends FilterException(s"${filterName} - ${message}")

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
      case _           => NumberFormat.getNumberInstance.format(number)
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
      case _           => None
    }
    val formatter = parameters.get("format") match {
      case Some(format: String) => {
        new DecimalFormat(format)
      }
      case Some(thing) => throw WrongParameterTypeException("format", "String")
      case _           => NumberFormat.getCurrencyInstance().asInstanceOf[DecimalFormat]
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

  override def apply(value: String, parameters: Map[String, Any]): String =
    value.capitalize
}

object CapitalizeFilter {
  def apply(): CapitalizeFilter = new CapitalizeFilter()
}

object LastFilter {
  def apply(): LastFilter = new LastFilter()
}

class LastFilter extends Filter {
  override def name = "last"

  override def apply(value: String, parameters: Map[String, Any]): String =
    value.last.toString

  override def applyIterable(value: Iterable[_], parameters: Map[String, Any]): Iterable[String] =
    if (value.nonEmpty) Seq(value.last.toString)
    else throw new IllegalArgumentException("Cannot call first on an empty collection.")

  override def applyMap(value: Map[_, _], parameters: Map[String, Any]): Map[String, String] =
    if (value.nonEmpty) {
      val tuple = value.last
      Map(tuple._1.toString -> tuple._2.toString)
    } else throw new IllegalArgumentException("Cannot call first on an empty collection.")
}

object FirstFilter {
  def apply(): FirstFilter = new FirstFilter()
}

class FirstFilter extends Filter {
  override def name = "first"

  override def apply(value: String, parameters: Map[String, Any]): String =
    value.head.toString

  override def applyIterable(value: Iterable[_], parameters: Map[String, Any]): Iterable[String] =
    if (value.nonEmpty) Seq(value.head.toString)
    else throw new IllegalArgumentException("Cannot call first on an empty collection.")

  override def applyMap(value: Map[_, _], parameters: Map[String, Any]): Map[String, String] =
    if (value.nonEmpty) {
      val tuple = value.head
      Map(tuple._1.toString -> tuple._2.toString)
    } else throw new IllegalArgumentException("Cannot call first on an empty collection.")
}

object ReverseFilter {
  def apply(): ReverseFilter = new ReverseFilter()
}

class ReverseFilter extends Filter {
  override def name = "reverse"

  override def apply(value: String, parameters: Map[String, Any]): String =
    value.reverse

  override def applyIterable(value: Iterable[_], parameters: Map[String, Any]): Iterable[String] = {
    @tailrec
    def iterate(acc: Seq[String], coll: Iterable[_]): Iterable[String] = {
      if (coll.isEmpty) acc
      else iterate(acc :+ coll.last.toString, coll.init)
    }

    if (value.nonEmpty) iterate(Seq.empty, value)
    else throw new IllegalArgumentException("Cannot call reverse on an empty list")
  }

  override def applyMap(value: Map[_, _], parameters: Map[String, Any]): Map[String, String] = {
    @tailrec
    def iterate(acc: Map[String, String], coll: Map[_, _]): Map[String, String] = {
      if (coll.isEmpty) acc
      else {
        val last = coll.last
        iterate(acc + ((last._1.toString, last._2.toString)), coll.init)
      }
    }
    if (value.nonEmpty) iterate(Map.empty, value)
    else throw new IllegalArgumentException("Cannot call reverse on an empty list")
  }
}

object TrimFilter {
  def apply(): TrimFilter = new TrimFilter()
}

class TrimFilter extends Filter {
  override def name = "trim"

  override def apply(value: String, parameters: Map[String, Any]): String =
    value.trim()
}

object UrlEncodeFilter {
  def apply(): UrlEncodeFilter = new UrlEncodeFilter()
}

class UrlEncodeFilter extends Filter {
  override def name = "url_encode"

  override def apply(value: String, parameters: Map[String, Any]): String =
    URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20")
}

object TitleFilter {
  def apply(): TitleFilter = new TitleFilter()
}

class TitleFilter extends Filter {
  override def name = "title"

  override def apply(value: String, parameters: Map[String, Any]): String =
    value.split(" ").map(_.capitalize).mkString(" ")
}

object AbsFilter {
  def apply(): AbsFilter = new AbsFilter()
}

class AbsFilter extends Filter {
  override def name = "abs"

  override def apply(value: String, parameters: Map[String, Any]): String =
    Math.abs(BigDecimal(value).toLong).toString
}

