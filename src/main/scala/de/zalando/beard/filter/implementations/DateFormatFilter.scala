package de.zalando.beard.filter.implementations

import java.time.{ZoneId, LocalDateTime, Instant}
import java.time.format.DateTimeFormatter

import de.zalando.beard.filter._

import scala.collection.immutable.Map
import scala.util.matching.Regex

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


  def resolveDateFormatting(value: String, formatOut: DateTimeFormatter): String = {
    val datePatterns: Map[String, String] = Map(
    // All formatters supported by DateTimeFormatter may be added in a form:
    // "FORMATTER" -> """REGEX"""
    // Grouping is not allowed: '(', ')' chars must be escaped, if used
    // EPOCH formatter is handled differently
      "EPOCH" -> """\d{8,}""",
      // yyyy-MM-dd
      "yyyy-MM-dd" -> """\d\d\d\d-\d\d-\d\d""",
      // yyyy-MM-dd HH:mm:ss
      "yyyy-MM-dd HH:mm:ss" -> """\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d""",
      // 20110203
      "BASIC_ISO_DATE" -> """\d{8}""",
      // 2011-2-13
      "yyyy-M-dd" -> """\d\d\d\d-\d-\d\d""",
      // 2011-12-3
      "yyyy-MM-d" -> """\d\d\d\d-\d\d-\d""",
      // 2011-2-3
      "yyyy-M-d" -> """\d\d\d\d-\d-\d""",
      // 03-02-2011
      "dd-MM-yyyy" -> """\d\d-\d\d-\d\d\d\d""",
      // 3-12-2011
      "d-MM-yyyy" -> """\d-\d\d-\d\d\d\d""",
      // 13-2-2011
      "dd-M-yyyy" -> """\d\d-\d-\d\d\d\d""",
      // 3-2-2011
      "d-M-yyyy" -> """\d-\d-\d\d\d\d""",
      // 04:05:06
      "HH:mm:ss" -> """\d\d:\d\d:\d\d""",
      // 4:5:6
      "H:m:s" -> """\d:\d:\d"""
    )

    val pattern = new Regex(datePatterns.values.mkString("^((", ")|(", "))$"))
    val a = pattern.findFirstMatchIn(value)

    if (a.isDefined) {
      val patternIndex = a.get.subgroups.indexOf(value, 1)
      val formatIn = datePatterns.slice(patternIndex-1, patternIndex).keys.mkString

      if (formatIn == "EPOCH")
        return getFormatFromMillis(value, formatOut)

      return getFormatFrom(value, formatIn, formatOut)
    }

    throw new DateFormatNotSupportedException(value)
  }

  def getFormatFromMillis(millisAsString: String, formatter: DateTimeFormatter): String = {
    val dateTime: Instant = Instant.ofEpochMilli(millisAsString.toLong)
    val formattedDate = formatter.format(LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault()))
    formattedDate
  }

  def getFormatFrom(data: String, formatIn: String, formatter: DateTimeFormatter): String = {
    try {
      val date = DateTimeFormatter.ofPattern(formatIn).parse(data)
      formatter.format(date)
    } catch {
      case e: Exception => throw new DateFormatNotSupportedException(formatIn)
    }
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
