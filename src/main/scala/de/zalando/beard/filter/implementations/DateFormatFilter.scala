package de.zalando.beard.filter.implementations

import java.time.{ZoneId, LocalDateTime, OffsetDateTime, Instant}
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

  case class DateFormatNotSupportedException(formatString: String) extends FilterException(formatString)

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
    // All formatters supported by DateTimeFormatter may be added in a form:
    //  """REGEX""" -> "FORMATTER"
    // Grouping is not allowed: '(', ')' chars must be escaped, if used
    val datePatterns: Map[String, String] = Map (
      // 981173106
      """\d{9,10}""" -> "EPOCH",
      // 981173106987
      """\d{12,13}""" -> "EPOCH_MILLI",
      // 20010203
      """\d{8}""" -> "yyyyMMdd",
     // 2001-02-03 04:05:06
      """\d{4}-\d\d-\d\d \d\d:\d\d:\d\d""" -> "yyyy-MM-dd HH:mm:ss",
      // 2001-02-03 04:05:06+01:00
      """\d{4}-\d\d-\d\d \d\d:\d\d:\d\d[+\-]?\d\d:?\d\d""" -> "yyyy-MM-dd HH:mm:ssZ",
       // 2001-02-03
      """\d{4}-\d\d-\d\d""" -> "yyyy-MM-dd",
      // 2001-02-03T04:05:06
      """\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d""" -> "ISO_LOCAL_DATE_TIME",
      // 2001-02-03T04:05:06+01:00'
      """\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d[+\-]?\d\d:?\d\d""" -> "ISO_OFFSET_DATE_TIME",
      // '2001-02-03T04:05:06.789Z'
      """\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d\.\d{1,3}Z""" -> "ISO_INSTANT",
      // '2001-02-03T04:05:06Z'
      """\d{4}-\d\d-\d\dT\d\d:\d\d:\d\dZ""" -> "ISO_INSTANT",
      // 2001-2-13
      """\d{4}-\d-\d\d""" -> "yyyy-M-dd",
      // 2001-12-3
      """\d{4}-\d\d-\d""" -> "yyyy-MM-d",
      // 2001-2-3
      """\d{4}-\d-\d""" -> "yyyy-M-d",
      // 03-02-2001
      """\d\d-\d\d-\d{4}""" -> "dd-MM-yyyy",
      // 03-02-2001 04:05:06
      """\d\d-\d\d-\d{4} \d\d:\d\d:\d\d""" -> "dd-MM-yyyy HH:mm:ss",
      // 3-12-2001
      """\d-\d\d-\d{4}""" -> "d-MM-yyyy",
      // 13-2-2001
      """\d\d-\d-\d{4}""" -> "dd-M-yyyy",
      // 3-2-2001
      """\d-\d-\d{4}""" -> "d-M-yyyy",
      // 04:05:06
      """\d\d:\d\d:\d\d""" -> "HH:mm:ss"
    )

    val pattern = new Regex(datePatterns.keys.mkString("^((", ")|(", "))$"))
    val a = pattern.findFirstMatchIn(value)

    if (a.isDefined) {
      val patternIndex = a.get.subgroups.indexOf(value, 1)
      val formatIn = datePatterns.slice(patternIndex-1, patternIndex).values.mkString

      return formatIn match {
        case "EPOCH" => getFormatFromEpoch(value, formatOut)
        case "EPOCH_MILLI" => getFormatFromMillis(value, formatOut)        
        case "ISO_INSTANT" => getFormatFromInstant(value, formatOut)
        case "ISO_LOCAL_DATE_TIME" => getFormatFromLocal(value, formatOut)
        case "ISO_OFFSET_DATE_TIME" => getFormatFromOffset(value, formatOut)
        case _ => getFormatFromPattern(value, formatIn, formatOut)
      }
    }

    throw new DateFormatNotSupportedException(value)
  }

  def getFormatFromMillis(millisAsString: String, formatter: DateTimeFormatter): String = {
    val dateTime: Instant = Instant.ofEpochMilli(millisAsString.toLong)
    val formattedDate = formatter.format(LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault()))
    formattedDate
  }

  def getFormatFromEpoch(epoch: String, formatter: DateTimeFormatter): String = {
    val dateTime: Instant = Instant.ofEpochSecond(epoch.toLong)
    val formattedDate = formatter.format(LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault()))
    formattedDate
  }

  def getFormatFromInstant(dateSrc: String, formatter: DateTimeFormatter): String = {
    val dateTime: Instant = Instant.parse(dateSrc)
    val formattedDate = formatter.format(LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault()))
    formattedDate
  }

  def getFormatFromLocal(dateSrc: String, formatter: DateTimeFormatter): String = {
    val dateTime: LocalDateTime = LocalDateTime.parse(dateSrc)
    val formattedDate = formatter.format(dateTime)
    formattedDate
  }

  def getFormatFromOffset(dateSrc: String, formatter: DateTimeFormatter): String = {
    val dateTime: OffsetDateTime = OffsetDateTime.parse(dateSrc)
    val formattedDate = formatter.format(dateTime)
    formattedDate
  }

  def getFormatFromPattern(dateSrc: String, formatIn: String, formatter: DateTimeFormatter): String = {
    try {
      val date = DateTimeFormatter.ofPattern(formatIn).parse(dateSrc)
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
