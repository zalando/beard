package de.zalando.beard.renderer

import de.zalando.beard.ast.{RenderStatement, Text, BeardTemplate}
import org.scalatest.{Matchers, FunSpec}
import scala.collection.immutable.Seq

/**
 * @author dpersa
 */
class CustomizableTemplateCompilerSpec extends FunSpec with Matchers {

  val compiler = DefaultTemplateCompiler

  describe("The template compiler") {

    describe("render statement") {
      it ("should inline render statements without parameters") {
        pending
      }
    }

    describe("extended templates") {

      it("should compile the extended templates") {

        val template = compiler.compile(TemplateName("/extends-example/index.beard")).get

        template should be(
          BeardTemplate(List(Text(s"<html>\n    "),
            Text("<div>This is the head</div>"),
            Text(s"\n    <body>\n        "),
            Text("<div>This is the header</div>"),
            Text("\n        <div id=\"main\">\n            "),
            Text("\n\n<div class=\"main-column\">\n    "),
            Text("\n\n<div>Hello Index</div>"),
            Text("\n</div>"),
            Text("\n        </div>\n    </body>\n<html>")),
            None, Seq(RenderStatement("/extends-example/head.beard"),
              RenderStatement("/extends-example/header.beard")))
        )
      }
    }
  }
}
