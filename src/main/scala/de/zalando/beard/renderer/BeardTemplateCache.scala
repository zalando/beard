package de.zalando.beard.renderer

import de.zalando.beard.ast.BeardTemplate

import scala.collection.immutable.Map

/**
 * @author dpersa
 */
class BeardTemplateCache {

  var cache = Map[TemplateName, BeardTemplate]()

  def add(templateName: TemplateName, template: BeardTemplate) = cache = cache.updated(templateName, template)

  def get(templateName: TemplateName) = cache.get(templateName)
}
