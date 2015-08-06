package de.zalando.beard.parser

import de.zalando.beard.ast._
import org.scalatest.{FunSpec, Matchers}
import scala.collection.immutable.Seq
import scala.io.Source

class BeardTemplateParserSpec extends FunSpec with Matchers {

  describe("The BeardTemplateParser") {
    describe("when parsing an empty string") {
      it("should return an empty BeardTemplate for an empty") {
        BeardTemplateParser("") should be(BeardTemplate(List.empty))
      }
    }

    describe("when parsing a string in brackets") {
      it("should return a BeardTemplate of an interpolation") {
        BeardTemplateParser("{{hello}}") should be(BeardTemplate(List(IdInterpolation(Identifier("hello")))))
      }
    }

    describe("when parsing a string with dots in brackets") {
      it("should return a BeardTemplate of an interpolation") {
        BeardTemplateParser("{{hello.world}}") should
          be(BeardTemplate(List(IdInterpolation(Identifier("hello"), Seq(Identifier("world"))))))
      }
    }

    describe("when parsing a simple string") {
      it("should return a BeardTemplate of a text") {
        BeardTemplateParser("hello") should be(BeardTemplate(List(Text("hello"))))
      }
    }

    describe("when parsing a string the contains brackets") {
      it("should return a BeardTemplate of a text and an interpolation") {
        BeardTemplateParser("hello {{world}}") should be(BeardTemplate(List(Text("hello "), IdInterpolation(Identifier("world")))))
      }
    }

    describe("when parsing a string the contains brackets") {
      it("should return a BeardTemplate of an interpolation and a text") {
        BeardTemplateParser("{{hello}} world") should be(BeardTemplate(List(IdInterpolation(Identifier("hello")), Text(" world"))))
      }
    }

    describe("when parsing a more complicated example") {
      it("should return a correct BeardTemplate of many interpolations and texts") {
        BeardTemplateParser("{{hello}} world {{how}} is {{the}} weather {{today}} is it good?") should
          be(BeardTemplate(List(
            IdInterpolation(Identifier("hello")),
            Text(" world "),
            IdInterpolation(Identifier("how")),
            Text(" is "),
            IdInterpolation(Identifier("the")),
            Text(" weather "),
            IdInterpolation(Identifier("today")),
            Text(" is it good?")
          )))
      }
    }

    describe("when parsing a string the contains special chars") {
      it("should return a BeardTemplate containing a text with those chars") {
        BeardTemplateParser("~!@#$%^&*()_+|-=\\<>,.?;':\"[]") should be(BeardTemplate(List(Text("~!@#$%^&*()_+|-=\\<>,.?;':\"[]"))))
      }
    }

    describe("when parsing a string the contains UTF-8 chars") {
      it("should return a BeardTemplate containing a text with those UTF-8 chars") {
        BeardTemplateParser("å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆ") should be(BeardTemplate(List(Text("å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆ"))))
      }
    }

    describe("when parsing a string that contains interpolations with attributes") {
      it("should allow attributes") {
        BeardTemplateParser("{{hello name=\"Dan\" color = 'blue'}}") should
          be(BeardTemplate(List(
            AttrInterpolation(Identifier("hello"), Seq(Attribute("name", "Dan"), Attribute("color", "blue")))
          )))
      }

      it("should skip white spaces, tabs and new lines inside an interpolation") {
        BeardTemplateParser("{{  hello   \t name=\"Dan\" \n color = 'blue' }}") should
          be(BeardTemplate(List(
            AttrInterpolation(Identifier("hello"), Seq(Attribute("name", "Dan"), Attribute("color", "blue"))
          ))))
      }

      describe("attribute values") {

        it("should preserve white spaces, tabs and new lines inside of an attribute value") {
          BeardTemplateParser("{{hello name=\"D a\tn\" color = 'bl\nue'}}") should
            be(BeardTemplate(List(
              AttrInterpolation(Identifier("hello"), Seq(Attribute("name", "D a\tn"), Attribute("color", "bl\nue")))
            )))
        }

        it("should allow UTF-8 chars inside of attribute values") {
          BeardTemplateParser("{{hello name=\"å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆDan\"}}") should
            be(BeardTemplate(List(
              AttrInterpolation(Identifier("hello"), Seq(Attribute("name", "å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆDan")))
            )))
        }

        it("should allow special chars inside of attribute values except quotes and double quotes") {
          BeardTemplateParser("{{hello name=\"~!@#$%^&*()_+|-=\\<>,.?;:[]\"}}") should
            be(BeardTemplate(List(
              AttrInterpolation(Identifier("hello"), Seq(Attribute("name", "~!@#$%^&*()_+|-=\\<>,.?;:[]")))
            )))
        }
      }

      describe("attribute identifiers") {

        it("should not start with a number") {
          BeardTemplateParser("{{9hello name=\"Dan\" color = 'blue'}}") should
            be(BeardTemplate(List(
              AttrInterpolation(Identifier("hello"), Seq(Attribute("name", "Dan"), Attribute("color", "blue"))
            ))))
        }

        it("should not allow special chars inside of attribute identifiers") {
          BeardTemplateParser("{{h!el!l%o name=\"Dan\" color = 'blue'}}") should
            be(BeardTemplate(List()))
        }
      }

      it("should return a BeardTemplate containing an interpolation with attributes") {
        BeardTemplateParser("more {{   hello   \n name=\"  He   llo  \" color = 'blue'}} world") should
          be(BeardTemplate(List(
            Text("more "),
            AttrInterpolation(Identifier("hello"), Seq(Attribute("name", "  He   llo  "), Attribute("color", "blue"))),
            Text(" world"))))
      }
    }

    describe("if block") {
      it ("should return a BeardTemplate containing an simple if block") {
        BeardTemplateParser("hello {{ if }} hello {{ endif}} end") should
          be(BeardTemplate(Seq(Text("hello "), IfStatement(Seq(Text(" hello "))), Text(" end"))))
      }

      it ("should return a BeardTemplate containing an simple if-else block") {
        BeardTemplateParser("block1 {{ if }} block2 {{ else }} block3 {{endif}} block4") should
          be(BeardTemplate(Seq(Text("block1 "), IfStatement(Seq(Text(" block2 ")), Seq(Text(" block3 "))), Text(" block4"))))
      }

      it ("should return a BeardTemplate containing nested if blocks") {
        BeardTemplateParser("hello {{ if }} block1 {{ if }} block2 {{endif}}block3{{ endif }} end") should
          be(BeardTemplate(List(Text("hello "),
                                IfStatement(List(Text(" block1 "),
                                            IfStatement(List(Text(" block2 "))),
                                            Text("block3"))),
                                Text(" end"))))
      }

      it ("should return a BeardTemplate containing deeper nested if blocks") {
        BeardTemplateParser("hello{{if}}block1{{if}}block2{{endif}}block3{{else}}{{if}}block4{{else}}block5{{endif}}{{endif}}end") should
          be(BeardTemplate(List(Text("hello"),
                                IfStatement(
                                    List(
                                      Text("block1"),
                                      IfStatement(List(Text("block2"))),
                                      Text("block3")),
                                    List(
                                      IfStatement(List(Text("block4")),
                                                  List(Text("block5"))))),
                                Text("end"))))
      }
    }

    describe("from file") {
      it ("should parse the template") {
        val template = Source.fromInputStream(getClass.getResourceAsStream(s"/templates/hello.beard")).mkString

        BeardTemplateParser(template) should be(
          BeardTemplate(List(
            Text("<div>somediv</div>\n"),
            AttrInterpolation(Identifier("block"), List(Attribute("id", "navigation"))),
            Text("\n    <ul>\n        <li>first</li>\n    </ul>\n"),
            IdInterpolation(Identifier("endblock")),
            Text("\n\n<p>Hello world</p>\n\n"),
            AttrInterpolation(Identifier("if"), List(Attribute("cond","users.empty"))),
            Text("\n    <div>No users</div>\n"),
            IdInterpolation(Identifier("else")),
            Text("\n    <div class=\"users\">\n    "),
            AttrInterpolation(Identifier("for"), List(Attribute("user","users"))),
            Text("\n        "),
            IdInterpolation(Identifier("user"), List(Identifier("name"))),
            AttrInterpolation(Identifier("unless"),
            List(Attribute("cond","user.last"))),
            Text(","),
            IdInterpolation(Identifier("endunless")),
            Text("\n    "),
            IdInterpolation(Identifier("endfor")),
            Text("\n    </div>\n"),
            IdInterpolation(Identifier("endif"))))
        )
      }
    }
  }
}
