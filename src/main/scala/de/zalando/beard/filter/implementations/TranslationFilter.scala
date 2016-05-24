package de.zalando.beard.filter.implementations

import java.util.{Locale, ResourceBundle}
import de.zalando.beard.filter.{Filter, ParameterMissingException}

/**
 * @author rweyand
 */
class TranslationFilter() extends Filter {

  override def name: String = "translate"

  override def apply(value: String, parameters: Map[String, Any]): String = {
    val localeStringParam = parameters.get("locale")
    val bundleNameParam = parameters.get("bundle")
    (localeStringParam, bundleNameParam) match {
      case (Some(locale: Locale), Some(bundleName: String)) => {
        fetchStringFromBundle(locale, bundleName, value)
      }
      case (Some(locale: String), Some(bundleName: String)) => {
        fetchStringFromBundle(Locale.forLanguageTag(locale), bundleName, value)
      }
      case (None, _) => throw new ParameterMissingException("resource bundle missing")
      case (_, _)    => throw new ParameterMissingException("paramters for translation missing")
    }
  }

  def fetchStringFromBundle(locale: Locale, bundleName: String, key: String): String = {
    ResourceBundle.getBundle(bundleName, locale).getString(key)
  }
}

object TranslationFilter {
  def apply(): TranslationFilter = new TranslationFilter()
}

