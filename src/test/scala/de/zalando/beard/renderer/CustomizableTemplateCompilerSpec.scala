package de.zalando.beard.renderer

import de.zalando.beard.ast._
import org.scalatest.{Matchers, FunSpec}
import scala.collection.immutable.{Seq, Map}

/**
 * @author dpersa
 */
class CustomizableTemplateCompilerSpec extends FunSpec with Matchers {

  val compiler = DefaultTemplateCompiler

  describe("#addContentForStatementsToMap") {
    it("should add the correct statements") {
      val contentForStatementsMap =
        Map {
          Identifier("hello") -> Seq(Text("one"))
        }

      val newContentForStatements = Seq(
        ContentForStatement(Identifier("world"), Seq(Text("second"))),
        ContentForStatement(Identifier("hello"), Seq(Text("third"))),
        ContentForStatement(Identifier("world"), Seq(Text("fourth"))),
        ContentForStatement(Identifier("some"), Seq(Text("fifth")))
      )

      val result = compiler.addContentForStatementsToMap(contentForStatementsMap, newContentForStatements)
      result should be(Map(
        Identifier("hello") -> Seq(Text("one")),
        Identifier("world") -> Seq(Text("second")),
        Identifier("some") -> Seq(Text("fifth"))
      ))
    }
  }

  describe("The template compiler") {

    describe("render statement") {
      it("should inline render statements without parameters") {
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

    describe("extended templates with blocks") {

      it("should compile the extended templates and override the blocks statements considering the contentFor statements") {

        val template = compiler.compile(TemplateName("/content-for-example/index.beard")).get

        template.statements should contain(BlockStatement(Identifier("head"), Seq(Text("\nsingle column head\n"))))
        template.statements should contain(BlockStatement(Identifier("header"), Seq(Text("index header"))))
        template.statements should contain(BlockStatement(Identifier("footer"), Seq(Text("application footer"))))
        template.statements should contain(BlockStatement(Identifier("leftColumn"), Seq(Text("index left column"))))
      }
    }
  }
}
