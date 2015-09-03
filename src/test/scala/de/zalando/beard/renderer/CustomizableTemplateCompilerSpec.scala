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

    describe("extended templates") {

      it ("should compile the extended templates") {

        val template = compiler.compile(TemplateName("/extends-example/index.beard")).get

        template should be(
          BeardTemplate(List(Text(s"<html>\n    "),
            RenderStatement("extends-example/head.beard"),
            Text(s"\n    <body>\n        "),
            RenderStatement("includes/header.beard"),
            Text("\n        <div id=\"main\">\n            "),
            Text("\n\n<div class=\"main-column\">\n    "),
            Text("\n\n<div>Hello Index</div>"),
            Text("\n</div>"),
            Text("\n        </div>\n    </body>\n<html>")))
        )
      }
    }
  }
}
