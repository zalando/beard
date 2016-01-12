package de.zalando.beard.parser

import de.zalando.beard.ast._
import org.scalatest.{FunSpec, Matchers}
import scala.collection.immutable.Seq
import scala.io.Source

class BeardTemplateParserSpec extends FunSpec with Matchers {

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

  describe("when parsing a template with curly brackets") {
    it("should return a BeardTemplate of an interpolation") {
      val expected = BeardTemplate(Seq(Text("{"), White(1), IdInterpolation(CompoundIdentifier("hello")), White(1), Text("}")))
      BeardTemplateParser("{ {{hello}} }") shouldBe expected
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
      BeardTemplateParser("hello {{world}}") should be(BeardTemplate(Seq(Text("hello"), White(1),
        IdInterpolation(CompoundIdentifier("world")))))
    }
  }

  describe("when parsing a string the contains brackets") {
    it("should return a BeardTemplate of an interpolation and a text") {
      BeardTemplateParser("{{hello}} world") should be(BeardTemplate(Seq(IdInterpolation(CompoundIdentifier("hello")), White(1),
        Text("world"))))
    }
  }

  describe("when parsing a more complicated example") {
    it("should return a correct BeardTemplate of many interpolations and texts") {
      BeardTemplateParser("{{hello}}world{{how}}is{{the}}weather{{today}}isitgood?") should
        be(BeardTemplate(Seq(
          IdInterpolation(CompoundIdentifier("hello")),
          Text("world"),
          IdInterpolation(CompoundIdentifier("how")),
          Text("is"),
          IdInterpolation(CompoundIdentifier("the")),
          Text("weather"),
          IdInterpolation(CompoundIdentifier("today")),
          Text("isitgood?")
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
          Text("more"), White(1),
          AttrInterpolation(Identifier("hello"), Seq(AttributeWithValue("name", "  He   llo  "), AttributeWithValue("color", "blue"))),
          White(1), Text("world"))))
    }
  }

  describe("if statement") {
    it("should return a BeardTemplate containing a simple if statement") {
      BeardTemplateParser("hello{{ if user.isCool }}hello{{ /if}}end") should
        be(BeardTemplate(Seq(Text("hello"), IfStatement(
          CompoundIdentifier("user", Seq("isCool")),
          Seq(Text("hello"))),
          Text("end"))))
    }

    it("should return a BeardTemplate containing a simple if-else statement") {
      BeardTemplateParser("block1{{ if user.isCool }}block2{{ else }}block3{{/if}}block4") should
        be(BeardTemplate(Seq(Text("block1"),
          IfStatement(
          CompoundIdentifier("user", Seq("isCool")),
          Seq(Text("block2")), Seq(Text("block3"))), Text("block4"))))
    }

    it("should return a BeardTemplate containing nested if statement") {
      BeardTemplateParser("hello{{ if user.isCool }}block1{{ if user.isNice }}block2{{/if}}block3{{ /if }}end") should
        be(BeardTemplate(Seq(Text("hello"),
          IfStatement(CompoundIdentifier("user", Seq("isCool")), Seq(Text("block1"),
            IfStatement(CompoundIdentifier("user", Seq("isNice")),
              Seq(Text("block2"))),
            Text("block3"))),
          Text("end"))))
    }

    it("should return a BeardTemplate containing deeper nested if statement") {
      BeardTemplateParser("hello{{if user.isCool}}block1{{if cool}}block2{{/if}}block3{{else}}{{if user.isNice}}block4{{else}}block5{{/if}}{{/if}}end") should
        be(BeardTemplate(Seq(Text("hello"),
          IfStatement(
            CompoundIdentifier("user", Seq("isCool")),
            Seq(
              Text("block1"),
              IfStatement(CompoundIdentifier("cool"), Seq(Text("block2"))),
              Text("block3")),
            Seq(
              IfStatement(
                CompoundIdentifier("user", Seq("isNice")),
                Seq(Text("block4")),
                Seq(Text("block5"))))),
          Text("end"))))
    }
  }

  describe("for statement") {
    it("should return a beard template containing a for statement") {
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

    it("should return a beard template containing nested for statements") {
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
    it("should return a beard template containing a simple render statement") {
      BeardTemplateParser( """<ul>{{render  "li-template"}}</ul>""") should
        be(BeardTemplate(Seq(
          Text("<ul>"),
          RenderStatement("li-template"),
          Text("</ul>")
        ), None, Seq(RenderStatement("li-template"))))
    }

    it("should return a beard template containing a render statement with attributes") {
      BeardTemplateParser( """<ul>{{render  "li-template" name="Dan" email=the.email}}</ul>""") should
        be(BeardTemplate(Seq(
          Text("<ul>"),
          RenderStatement("li-template", Seq(
            AttributeWithValue("name", "Dan"),
            AttributeWithIdentifier("email", CompoundIdentifier("the", Seq("email"))))),
          Text("</ul>")
        ), None, Seq(RenderStatement("li-template", Seq(
          AttributeWithValue("name", "Dan"),
          AttributeWithIdentifier("email", CompoundIdentifier("the", Seq("email"))))))))
    }
  }

  describe("extends statement") {
    it("should return a beard template containing an extends statement") {
      BeardTemplateParser( """{{extends "layout"}}<div>Hello</div>""") should
        be(BeardTemplate(Seq(Text("<div>Hello</div>")), Some(ExtendsStatement("layout"))))
    }

    it("should not allow two extends statements") {
      pending
    }
  }

  describe("yield statement") {
    it("should return a beard template containing an yield statement") {
      BeardTemplateParser( """{{extends "layout"}}<div>Hello{{yield}}</div>""") should
        be(BeardTemplate(Seq(Text("<div>Hello"), YieldStatement(), Text("</div>")), Some(ExtendsStatement("layout"))))
    }
  }

  describe("block statement") {
    it("should return a beard template containing a simple block statement") {
      BeardTemplateParser("<ul>{{block header}}\n<div>Hello</div>{{/block}}</ul>") should
        be(BeardTemplate(Seq(
          Text("<ul>"),
          BlockStatement(Identifier("header"), Seq(NewLine(1), Text("<div>Hello</div>"))),
          Text("</ul>")
        )))
    }

    it("should not let the block statements to be nested") {
      pending
    }
  }

  describe("contentFor statement") {
    it("should return a beard template containing a simple contentFor statement") {
      BeardTemplateParser( """{{contentFor header}}<div>Hello</div>{{/contentFor}}<body>Hello</body>""") should
        be(BeardTemplate(Seq(
          Text("<body>Hello</body>")
        ), contentForStatements = Seq(ContentForStatement(Identifier("header"), Seq(Text("<div>Hello</div>"))))))
    }

    it("should not consider the new lines before the contentFor statements") {
      BeardTemplateParser("\n{{contentFor header}}<div>Hello</div>{{/contentFor}}<body>Hello</body>") should
        be(BeardTemplate(Seq(
          Text("<body>Hello</body>")
        ), contentForStatements = Seq(ContentForStatement(Identifier("header"), Seq(Text("<div>Hello</div>"))))))
    }

    it("should not consider the new lines in between extends and contentFor statements") {
      BeardTemplateParser("{{extends \"layout\"}}\n{{contentFor header}}<div>Hello</div>{{/contentFor}}<body>Hello</body>") should
        be(BeardTemplate(Seq(
          Text("<body>Hello</body>")
        ), extended = Some(ExtendsStatement("layout")),
          contentForStatements = Seq(ContentForStatement(Identifier("header"), Seq(Text("<div>Hello</div>"))))))
    }

    it("should not let the contentFor statements to be nested") {
      pending
    }
  }

  describe("from file") {
    it("should parse the template") {
      val template = Source.fromInputStream(getClass.getResourceAsStream("/templates/hello.beard")).mkString

      BeardTemplateParser(template).statements should be(
        Seq(
          Text("<div>somediv</div>"),
          BlockStatement(Identifier("navigation"), List(NewLine(1),
            White(4),
            Text("<ul>"),
            NewLine(1),
            White(8),
            Text("<li>first</li>"),
            NewLine(1),
            White(4),
            Text("</ul>"))),
          NewLine(2),
          Text("<p>Hello"),
          White(1),
          Text("world</p>"),
          NewLine(2),
          IfStatement(
            CompoundIdentifier("usersExist"),
            List(NewLine(1),
            White(4),
            Text("<div>No"),
            White(1),
            Text("users</div>"),
            NewLine(1)), List(NewLine(1),
            White(4),
            Text("<div"),
            White(1),
            Text("class=\"users\">"),
            ForStatement(Identifier("user"), CompoundIdentifier("users", List()), List(NewLine(1),
              White(8),
              IdInterpolation(CompoundIdentifier("user", List("name"))),
              IfStatement(
              CompoundIdentifier("user", Seq("isLast")),
                List(Text(",")), List()), White(8),
              RenderStatement("user-details", List(AttributeWithIdentifier("user", CompoundIdentifier("user", List())),
                AttributeWithValue("class", "default"))))),
            NewLine(1),
            White(4),
            Text("</div>"),
            NewLine(1)))
        )
      )
    }
  }

  describe("from file") {
    it("should parse the layout with partial") {
      val template = Source.fromInputStream(getClass.getResourceAsStream("/templates/layout-with-partial.beard")).mkString

      BeardTemplateParser(template).statements should be(
        Seq(
          Text("<!DOCTYPE"),
          White(1),
          Text("html>"),
          NewLine(1),
          Text("<html>"),
          NewLine(1),
          Text("<head>"),
          NewLine(1),
          White(4),
          Text("<meta"),
          White(1),
          Text("charset=\"utf-8\"/>"),
          NewLine(1),
          White(4),
          Text("<meta"),
          White(1),
          Text("name=\"viewport\""),
          White(1),
          Text("content=\"width=device-width,"),
          White(1),
          Text("initial-scale=1.0\"/>"),
          NewLine(1),
          White(4),
          Text("<meta"),
          White(1),
          Text("http-equiv=\"X-UA-Compatible\""),
          White(1),
          Text("content=\"IE=Edge\"/>"),
          NewLine(1),
          White(4),
          Text("<title>"),
          IdInterpolation(CompoundIdentifier("example", List("title"))),
          White(1),
          Text("-"),
          White(1),
          Text("Pebble</title>"),
          NewLine(1),
          White(4),
          Text("<link"),
          White(1),
          Text("rel=\"stylesheet\""),
          White(1),
          Text("href=\"/webjars/bootstrap/3.0.1/css/bootstrap.min.css\""),
          White(1),
          Text("media=\"screen\"/>"),
          NewLine(1),
          Text("</head>"),
          NewLine(1),
          Text("<body>"),
          NewLine(1),
          Text("<div"),
          White(1),
          Text("class=\"container\">"),
          RenderStatement("/templates/_partial.beard", List(AttributeWithIdentifier("title", CompoundIdentifier("example", List("title"))),
            AttributeWithIdentifier("presentations", CompoundIdentifier("example", List("presentations"))))),
          NewLine(1),
          Text("</div>"),
          RenderStatement("/templates/_footer.beard", List()),
          RenderStatement("/templates/_footer.beard", List()),
          RenderStatement("/templates/_footer.beard", List()),
          NewLine(1),
          Text("</body>"),
          NewLine(1),
          Text("</html>")
        )
      )
    }
  }
}
