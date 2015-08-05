package de.zalando.beard.renderer

import de.zalando.beard.ast._
import org.scalatest.{FunSpec, Matchers}

/**
 * @author dpersa
 */
class BeardTemplateRendererTest extends FunSpec with Matchers {

  val renderer = new BeardTemplateRenderer

  describe("BeardTemplateRendererTest") {

    it("should render") {
      //val template = BeardTemplate(List(Text("Hello "), IdInterpolation(Identifier("name"))))
      //renderer.render(template, Map("name" -> "world")) should be("Hello world")
    }
  }
}
