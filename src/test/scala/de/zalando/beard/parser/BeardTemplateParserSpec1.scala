package de.zalando.beard.parser

import de.zalando.beard.ast._
import org.scalatest.{FunSpec, Matchers}

import scala.collection.immutable.Seq
import scala.io.Source

class BeardTemplateParserSpec1 extends FunSpec with Matchers {





  describe("from file") {
    it("should parse the template") {
      val template = Source.fromInputStream(getClass.getResourceAsStream("/templates/hello.beard")).mkString

      BeardTemplateParser(template) should be(
        BeardTemplate(Seq(
          Text("<div>somediv</div>"), NewLine(1),
          BlockStatement(Identifier("navigation"), Seq(Text("    <ul>"), NewLine(1), Text("        <li>first</li>"), NewLine(1), Text("    </ul>"),  NewLine(1))),
          NewLine(2), Text("<p>Hello world</p>"),  NewLine(2),
          IfStatement(
            Seq( NewLine(1), Text("    <div>No users</div>"),  NewLine(1)),
            Seq( NewLine(1), Text("    <div class=\"users\">"),  NewLine(1), Text("    "),
              ForStatement(Identifier("user"), CompoundIdentifier("users"),
                Seq( NewLine(1), Text("        "),
                  IdInterpolation(CompoundIdentifier("user", Seq("name"))),
                  IfStatement(Seq(Text(","))),
                  NewLine(1), Text("        "),
                  RenderStatement("user-details", Seq(AttributeWithIdentifier("user", CompoundIdentifier("user")),
                    AttributeWithValue("class", "default"))),
                  NewLine(1), Text("    ")
                )
              ),
              NewLine(1), Text("    </div>"),  NewLine(1))
          )
        ), Some(ExtendsStatement("layout"))
        )
      )
    }
  }
//
//  describe("from file") {
//    it("should parse the layout with partial") {
//      val template = Source.fromInputStream(getClass.getResourceAsStream("/templates/layout-with-partial.beard")).mkString
//
//      BeardTemplateParser(template) should be(
//        BeardTemplate(Seq(
//          Text("<!DOCTYPE html>\n<html>\n<head>\n    <meta charset=\"utf-8\"/>\n    <meta name=\"viewport\"" +
//            " content=\"width=device-width, initial-scale=1.0\"/>\n" +
//            "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\"/>\n    <title>"),
//          IdInterpolation(CompoundIdentifier("example", List("title"))),
//          Text(" - Pebble</title>\n    <link rel=\"stylesheet\" href=\"/webjars/bootstrap/3.0.1/css/bootstrap.min.css\"" +
//            " media=\"screen\"/>\n</head>\n<body>\n<div class=\"container\">\n    "),
//          RenderStatement("/templates/_partial.beard", List(AttributeWithIdentifier("title", CompoundIdentifier("example", List("title"))),
//            AttributeWithIdentifier("presentations", CompoundIdentifier("example", List("presentations"))))),
//          Text("\n</div>\n    "),
//          RenderStatement("/templates/_footer.beard"), Text("\n    "),
//          RenderStatement("/templates/_footer.beard"), Text("\n    "),
//          RenderStatement("/templates/_footer.beard"),
//          Text("\n</body>\n</html>")
//        ), None, Seq(
//          RenderStatement("/templates/_partial.beard", List(AttributeWithIdentifier("title", CompoundIdentifier("example", List("title"))),
//            AttributeWithIdentifier("presentations", CompoundIdentifier("example", List("presentations"))))),
//          RenderStatement("/templates/_footer.beard"),
//          RenderStatement("/templates/_footer.beard"),
//          RenderStatement("/templates/_footer.beard"))
//        )
//      )
//    }
//  }
}
