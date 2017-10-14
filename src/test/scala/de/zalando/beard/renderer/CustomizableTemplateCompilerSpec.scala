package de.zalando.beard.renderer

import de.zalando.beard.ast._
import org.scalatest.{FunSpec, Matchers}

import scala.collection.immutable.{Map, Seq}
import scala.util.Failure

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

  describe("#getMergedTokens") {
    it("should merge a text with a new line") {
      val mergeTextsStatements = compiler.concatTexts(Seq(Text("hello"), NewLine(2)))
      mergeTextsStatements.should(be(Seq[Statement](Text("hello\n\n"))))
    }

    it("should merge more texts with a new lines") {
      val mergeTextsStatements = compiler.concatTexts(Seq(Text("hello"), NewLine(2),
        Text("world"), NewLine(1), NewLine(1), Text("!")))
      mergeTextsStatements.should(be(Seq[Statement](Text("hello\n\nworld\n\n!"))))
    }

    it("should merge more texts with a new lines and statements") {
      val mergeTextsStatements = compiler.concatTexts(Seq(Text("hello"), NewLine(2),
        YieldStatement(), Text("world"), NewLine(1), YieldStatement(), NewLine(1), Text("!")))
      mergeTextsStatements.should(be(Seq[Statement](Text("hello\n\n"), YieldStatement(), Text("world\n"),
        YieldStatement(), Text("\n!"))))
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
          BeardTemplate(
            List(
              Text("""<html>
                    |    <div>This is the head</div>
                    |    <body>
                    |        <div>This is the header</div>
                    |        <div id="main">
                    |            <div class="main-column">
                    |                <div>Hello Index</div>
                    |            </div>
                    |        </div>
                    |    </body>
                    |<html>""".stripMargin)),
            None, Seq(
              RenderStatement("/extends-example/head.beard"),
              RenderStatement("/extends-example/header.beard")))
        )
      }
    }

    describe("extended templates with blocks") {

      it("should compile the extended templates and override the blocks statements considering the contentFor statements") {

        val template = compiler.compile(TemplateName("/content-for-example/index.beard")).get

        template.statements.head.asInstanceOf[Text].text should include("single column head")
        template.statements.head.asInstanceOf[Text].text should include("index header")
        template.statements.head.asInstanceOf[Text].text should include("application footer")
        template.statements.head.asInstanceOf[Text].text should include("index left column")
      }
    }

    describe("if the template is not found") {

      it ("should return failure") {
        val exception =
          compiler.compile(TemplateName("some-name")) match {
            case Failure(ex) => ex
            case _           => fail
          }
        exception shouldBe a[TemplateNotFoundException]
        exception.getMessage should equal("Expected to find template 'some-name' in file 'some-name', file not found on classpath")
      }
    }
  }
}
