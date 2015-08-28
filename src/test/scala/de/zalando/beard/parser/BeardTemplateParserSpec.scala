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
        BeardTemplateParser("{{hello}}") should be(BeardTemplate(Seq(IdInterpolation(CompoundIdentifier("hello")))))
      }
    }

    describe("when parsing a string with dots in brackets") {
      it("should return a BeardTemplate of an interpolation") {
        BeardTemplateParser("{{hello.world}}") should
          be(BeardTemplate(Seq(IdInterpolation(CompoundIdentifier("hello", Seq("world"))))))
      }
    }

    describe("when parsing a simple string") {
      it("should return a BeardTemplate of a text") {
        BeardTemplateParser("hello") should be(BeardTemplate(Seq(Text("hello"))))
      }
    }

    describe("when parsing a string the contains brackets") {
      it("should return a BeardTemplate of a text and an interpolation") {
        BeardTemplateParser("hello {{world}}") should be(BeardTemplate(Seq(Text("hello "), IdInterpolation(CompoundIdentifier("world")))))
      }
    }

    describe("when parsing a string the contains brackets") {
      it("should return a BeardTemplate of an interpolation and a text") {
        BeardTemplateParser("{{hello}} world") should be(BeardTemplate(Seq(IdInterpolation(CompoundIdentifier("hello")), Text(" world"))))
      }
    }

    describe("when parsing a more complicated example") {
      it("should return a correct BeardTemplate of many interpolations and texts") {
        BeardTemplateParser("{{hello}} world {{how}} is {{the}} weather {{today}} is it good?") should
          be(BeardTemplate(Seq(
            IdInterpolation(CompoundIdentifier("hello")),
            Text(" world "),
            IdInterpolation(CompoundIdentifier("how")),
            Text(" is "),
            IdInterpolation(CompoundIdentifier("the")),
            Text(" weather "),
            IdInterpolation(CompoundIdentifier("today")),
            Text(" is it good?")
          )))
      }
    }

    describe("when parsing a string the contains special chars") {
      it("should return a BeardTemplate containing a text with those chars") {
        BeardTemplateParser("~!@#$%^&*()_+|-=\\<>,.?;':\"[]") should be(BeardTemplate(Seq(Text("~!@#$%^&*()_+|-=\\<>,.?;':\"[]"))))
      }
    }

    describe("when parsing a string the contains UTF-8 chars") {
      it("should return a BeardTemplate containing a text with those UTF-8 chars") {
        BeardTemplateParser("å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆ") should be(BeardTemplate(Seq(Text("å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆ"))))
      }
    }

    describe("when parsing a string that contains interpolations with attributes") {
      it("should allow attributes") {
        BeardTemplateParser("{{hello name=\"Dan\" color = 'blue'}}") should
          be(BeardTemplate(Seq(
            AttrInterpolation(Identifier("hello"), Seq(AttributeWithValue("name", "Dan"), AttributeWithValue("color", "blue")))
          )))
      }

      it("should skip white spaces, tabs and new lines inside an interpolation") {
        BeardTemplateParser("{{  hello   \t name=\"Dan\" \n color = 'blue' }}") should
          be(BeardTemplate(Seq(
            AttrInterpolation(Identifier("hello"), Seq(AttributeWithValue("name", "Dan"), AttributeWithValue("color", "blue"))
          ))))
      }

      describe("attribute values") {

        describe("attribute values as text") {

          it("should preserve white spaces, tabs and new lines inside of an attribute value") {
            BeardTemplateParser("{{hello name=\"D a\tn\" color = 'bl\nue'}}") should
              be(BeardTemplate(Seq(
                AttrInterpolation(Identifier("hello"), Seq(AttributeWithValue("name", "D a\tn"), AttributeWithValue("color", "bl\nue")))
              )))
          }

          it("should allow UTF-8 chars inside of attribute values") {
            BeardTemplateParser("{{hello name=\"å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆDan\"}}") should
              be(BeardTemplate(Seq(
                AttrInterpolation(Identifier("hello"), Seq(AttributeWithValue("name", "å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆDan")))
              )))
          }

          it("should allow special chars inside of attribute values except quotes and double quotes") {
            BeardTemplateParser("{{hello name=\"~!@#$%^&*()_+|-=\\<>,.?;:[]\"}}") should
              be(BeardTemplate(Seq(
                AttrInterpolation(Identifier("hello"), Seq(AttributeWithValue("name", "~!@#$%^&*()_+|-=\\<>,.?;:[]")))
              )))
          }
        }

        describe("attribute values as compound identifiers") {

          it("should allow compound identifiers as attribute values") {
            BeardTemplateParser("{{hello name=the.name color = the.color}}") should
              be(BeardTemplate(Seq(
                AttrInterpolation(Identifier("hello"), Seq(AttributeWithIdentifier("name", CompoundIdentifier("the", Seq("name"))),
                  AttributeWithIdentifier("color", CompoundIdentifier("the", Seq("color")))))
              )))
          }

          it("should allow mixing compound identifiers with strings as attribute values") {
            BeardTemplateParser("{{hello name=the.name color = \"red\"}}") should
              be(BeardTemplate(Seq(
                AttrInterpolation(Identifier("hello"), Seq(AttributeWithIdentifier("name", CompoundIdentifier("the", Seq("name"))),
                  AttributeWithValue("color", "red")))
              )))
          }

        }
      }

      describe("attribute identifiers") {

        it("should not start with a number") {
          BeardTemplateParser("{{9hello name=\"Dan\" color = 'blue'}}") should
            be(BeardTemplate(Seq(
              AttrInterpolation(Identifier("hello"), Seq(AttributeWithValue("name", "Dan"), AttributeWithValue("color", "blue"))
            ))))
        }

//        it("should not allow special chars inside of attribute identifiers") {
//          BeardTemplateParser("{{h!el!l%o name=\"Dan\" color = 'blue'}}") should
//            be(BeardTemplate(Seq()))
//        }
      }

      it("should return a BeardTemplate containing an interpolation with attributes") {
        BeardTemplateParser("more {{   hello   \n name=\"  He   llo  \" color = 'blue'}} world") should
          be(BeardTemplate(Seq(
            Text("more "),
            AttrInterpolation(Identifier("hello"), Seq(AttributeWithValue("name", "  He   llo  "), AttributeWithValue("color", "blue"))),
            Text(" world"))))
      }
    }

    describe("if statement") {
      it ("should return a BeardTemplate containing a simple if statement") {
        BeardTemplateParser("hello {{ if }} hello {{ /if}} end") should
          be(BeardTemplate(Seq(Text("hello "), IfStatement(Seq(Text(" hello "))), Text(" end"))))
      }

      it ("should return a BeardTemplate containing a simple if-else statement") {
        BeardTemplateParser("block1 {{ if }} block2 {{ else }} block3 {{/if}} block4") should
          be(BeardTemplate(Seq(Text("block1 "), IfStatement(Seq(Text(" block2 ")), Seq(Text(" block3 "))), Text(" block4"))))
      }

      it ("should return a BeardTemplate containing nested if statement") {
        BeardTemplateParser("hello {{ if }} block1 {{ if }} block2 {{/if}}block3{{ /if }} end") should
          be(BeardTemplate(Seq(Text("hello "),
                                IfStatement(Seq(Text(" block1 "),
                                            IfStatement(Seq(Text(" block2 "))),
                                            Text("block3"))),
                                Text(" end"))))
      }

      it ("should return a BeardTemplate containing deeper nested if statement") {
        BeardTemplateParser("hello{{if}}block1{{if}}block2{{/if}}block3{{else}}{{if}}block4{{else}}block5{{/if}}{{/if}}end") should
          be(BeardTemplate(Seq(Text("hello"),
                                IfStatement(
                                    Seq(
                                      Text("block1"),
                                      IfStatement(Seq(Text("block2"))),
                                      Text("block3")),
                                    Seq(
                                      IfStatement(Seq(Text("block4")),
                                                  Seq(Text("block5"))))),
                                Text("end"))))
      }
    }

    describe("for statement") {
      it ("should return a beard template containing a for statement") {
        BeardTemplateParser("<ul>{{for user in users}}<li>{{user.name}}</li>{{/for}}</ul>") should
         be(BeardTemplate(Seq(
                Text("<ul>"),
                ForStatement(Identifier("user"), CompoundIdentifier("users"),
                  Seq(Text("<li>"), IdInterpolation(CompoundIdentifier("user", Seq("name"))), Text("</li>"))
                ),
                Text("</ul>")
              ))
         )
      }

      it ("should return a beard template containing nested for statements") {
        BeardTemplateParser("<ul>{{for user in users}}<li>{{user.name}}{{for book in user.books}}{{book.name}}{{/for}}</li>{{/for}}</ul>") should
          be(BeardTemplate(Seq(
            Text("<ul>"),
            ForStatement(Identifier("user"), CompoundIdentifier("users"),
              Seq(
                Text("<li>"),
                IdInterpolation(CompoundIdentifier("user", Seq("name"))),
                ForStatement(Identifier("book"), CompoundIdentifier("user", Seq("books")), Seq(
                  IdInterpolation(CompoundIdentifier("book", Seq("name")))
                )),
                Text("</li>"))
            ),
            Text("</ul>")
          ))
          )
      }
    }

    describe("render statement") {
      it ("should return a beard template containing a simple render statement") {
        BeardTemplateParser("""<ul>{{render  "li-template"}}</ul>""") should
        be(BeardTemplate(Seq(
          Text("<ul>"),
          RenderStatement("li-template"),
          Text("</ul>")
        )))
      }

      it ("should return a beard template containing a render statement with attributes") {
        BeardTemplateParser("""<ul>{{render  "li-template" name="Dan" email=the.email}}</ul>""") should
          be(BeardTemplate(Seq(
            Text("<ul>"),
            RenderStatement("li-template", Seq(
              AttributeWithValue("name", "Dan"),
              AttributeWithIdentifier("email", CompoundIdentifier("the", Seq("email"))))),
            Text("</ul>")
          )))
      }
    }

    describe("extends statement") {
      it ("should return a beard template containing an extends statement") {
        BeardTemplateParser("""{{extends "layout"}}<div>Hello</div>""") should
          be(BeardTemplate(Seq(
            ExtendsStatement("layout"),
            Text("<div>Hello</div>")
          )))
      }
    }

    describe("block statement") {
      it ("should return a beard template containing a simple block statement") {
        BeardTemplateParser("""<ul>{{block header}}<div>Hello</div>{{/block}}</ul>""") should
          be(BeardTemplate(Seq(
            Text("<ul>"),
            BlockStatement(Identifier("header"), Seq(Text("<div>Hello</div>"))),
            Text("</ul>")
          )))
      }

      it ("should not let the block statements to be nested") {}
    }

    describe("from file") {
      it ("should parse the template") {
        val template = Source.fromInputStream(getClass.getResourceAsStream(s"/templates/hello.beard")).mkString

        BeardTemplateParser(template) should be(
          BeardTemplate(Seq(
            ExtendsStatement("layout"),
            Text("\n<div>somediv</div>\n"),
            BlockStatement(Identifier("navigation"), Seq(Text("\n    <ul>\n        <li>first</li>\n    </ul>\n"))),
            Text("\n\n<p>Hello world</p>\n\n"),
            IfStatement(
              Seq(Text("\n    <div>No users</div>\n")),
              Seq(Text("\n    <div class=\"users\">\n    "),
                   ForStatement(Identifier("user"), CompoundIdentifier("users"),
                     Seq(Text("\n        "),
                         IdInterpolation(CompoundIdentifier("user", Seq("name"))),
                         IfStatement(Seq(Text(","))),
                         Text("\n        "),
                         RenderStatement("user-details", Seq(AttributeWithIdentifier("user", CompoundIdentifier("user")), AttributeWithValue("class", "default"))),
                         Text("\n    ")
                     )
                   ),
                   Text("\n    </div>\n"))
              )
            )
          )
        )
      }
    }

    describe("from file") {
      it("should parse the layout with partial") {
        val template = Source.fromInputStream(getClass.getResourceAsStream(s"/templates/layout-with-partial.beard")).mkString

        BeardTemplateParser(template) should be(
          BeardTemplate(Seq(
            Text("<!DOCTYPE html>\n<html>\n<head>\n    <meta charset=\"utf-8\"/>\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
              "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\"/>\n    <title>"),
            IdInterpolation(CompoundIdentifier("example", List("title"))),
            Text(" - Pebble</title>\n    <link rel=\"stylesheet\" href=\"/webjars/bootstrap/3.0.1/css/bootstrap.min.css\" media=\"screen\"/>\n</head>\n<body>\n<div class=\"container\">\n    "),
            RenderStatement("partial", List(AttributeWithIdentifier("title", CompoundIdentifier("example", List("title"))), AttributeWithIdentifier("presentations", CompoundIdentifier("example", List("presentations"))))),
            Text("\n</div>\n<script src=\"/webjars/jquery/2.0.2/jquery.min.js\"></script>\n<script src=\"/webjars/bootstrap/3.0.1/js/bootstrap.min.js\"></script>\n</body>\n</html>")
          )
          )
        )
      }
    }
  }
}
