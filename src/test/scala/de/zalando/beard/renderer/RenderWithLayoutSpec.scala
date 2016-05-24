package de.zalando.beard.renderer

import org.scalatest.{Matchers, FunSpec}

/**
 * @author dpersa
 */
class RenderWithLayoutSpec extends FunSpec with Matchers {

  val loader = new ClasspathTemplateLoader(
    templatePrefix = "/layout-render",
    templateSuffix = ".beard"
  )

  val templateCompiler = new CustomizableTemplateCompiler(templateLoader = loader)
  val renderer = new BeardTemplateRenderer(templateCompiler)

  val template = templateCompiler.compile(TemplateName("/presentations/index")).get
  val applicationLayout = templateCompiler.compile(TemplateName("/layouts/application")).get
  val doubleColumnLayout = templateCompiler.compile(TemplateName("/layouts/double-column")).get

  val context: Map[String, Map[String, Object]] = Map("example" -> Map("title" -> "Title"))

  it("should render the template with the application layout") {

    val result = renderer.render(template, StringWriterRenderResult(), context, Some(applicationLayout))

    result.toString should include("<div>application head layout</div>")
    result.toString should include("<div>presentations</div>")
  }

  it("should render the template with the double-column layout") {

    val result = renderer.render(template, StringWriterRenderResult(), context, Some(doubleColumnLayout))

    result.toString should include("<div>double column head layout</div>")
    result.toString should include("<div>presentations</div>")
  }
}
