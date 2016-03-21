package de.zalando.beard.filter.implementations

import java.time.{ZoneId, LocalDateTime, Instant}
import java.time.format.DateTimeFormatter

import de.zalando.beard.filter._

import scala.collection.immutable.Map
import scala.util.matching.Regex
import scala.util.{Success, Try}

/**
  * Created by rweyand on 3/15/16.
  */
class DateFormatFilter extends Filter {
  // {{ now | date format=format.Variable}}
  override def name = "date"

  case class DateFormatNotSupportedException(formatString: String) extends FilterException

  override def apply(value: String, parameters: Map[String, Any]): String =
    parameters.get("format") match {
      // format given as static string in the template
      case Some(format: String) =>  {
        val dateTimeFormatter = getDateTimeFormatter(format)
        resolveDateFormatting(value, dateTimeFormatter)
      }
      // format given as variable (resolves to nested Option)
      case Some(Some(format)) => {
        val dateTimeFormatter = getDateTimeFormatter(format.asInstanceOf[String])
        resolveDateFormatting(value, dateTimeFormatter)
      }
      case Some(thing) => throw WrongParameterTypeException("format", "String")
      case None => throw ParameterMissingException("format")
    }

  def resolveDateFormatting(value: String, formatter: DateTimeFormatter): String = {
    if(value.matches("^\\d*$")) {
      getFormatFromMillis(value, formatter)
    } else {
      getFormatFromISO(value, formatter)
    }
  }

  def getFormatFromMillis(millisAsString: String, formatter: DateTimeFormatter): String = {
      val dateTime: Instant = Instant.ofEpochMilli(millisAsString.toLong)
      val formattedDate = formatter.format(LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault()))
      formattedDate
  }

  def getFormatFromISO(isoString: String, formatter: DateTimeFormatter): String = {
      val date = DateTimeFormatter.ISO_DATE_TIME.parse(isoString)
      val formattedDate = formatter.format(date)
      formattedDate
  }

  def getDateTimeFormatter(format: String): DateTimeFormatter = {
    try {
      DateTimeFormatter.ofPattern(format)
    } catch {
      case e: Exception => throw new DateFormatNotSupportedException(format)
    }
  }
}

object DateFormatFilter {

  def apply(): DateFormatFilter = new DateFormatFilter()
}
