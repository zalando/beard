package de.zalando.beard

import de.zalando.beard.ast.{BeardTemplate, Identifier, Interpolation, Text}
import org.scalatest._

class BeardTemplateParserSpec extends FunSpec with Matchers {

  describe("The BeardTemplateParser") {
    describe("when parsing an empty string") {
      it("should return an empty BeardTemplate for an empty") {
        BeardTemplateParser("") should be(BeardTemplate(List.empty))
      }
    }

    describe("when parsing a string in brackets") {
      it("should return a BeardTemplate of an interpolation") {
        BeardTemplateParser("{{hello}}") should be(BeardTemplate(List(Interpolation(Identifier("hello")))))
      }
    }

    describe("when parsing a simple string") {
      it("should return a BeardTemplate of a text") {
        BeardTemplateParser("hello") should be(BeardTemplate(List(Text("hello"))))
      }
    }

    describe("when parsing a string the contains brackets") {
      it("should return a BeardTemplate of a text and an interpolation") {
        BeardTemplateParser("hello {{world}}") should be(BeardTemplate(List(Text("hello "), Interpolation(Identifier("world")))))
      }
    }

    describe("when parsing a string the contains brackets") {
      it("should return a BeardTemplate of an interpolation and a text") {
        BeardTemplateParser("{{hello}} world") should be(BeardTemplate(List(Interpolation(Identifier("hello")), Text(" world"))))
      }
    }

    describe("when parsing a more complicated example") {
      it("should return a correct BeardTemplate of many interpolations and texts") {
        BeardTemplateParser("{{hello}} world {{how}} is {{the}} weather {{today}} is it good?") should
          be(BeardTemplate(List(
            Interpolation(Identifier("hello")),
            Text(" world "),
            Interpolation(Identifier("how")),
            Text(" is "),
            Interpolation(Identifier("the")),
            Text(" weather "),
            Interpolation(Identifier("today")),
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
            Interpolation(Identifier("hello"), Map("name" -> "Dan", "color" -> "blue"))
          )))
      }

      it("should skip white spaces, tabs and new lines inside an interpolation") {
        BeardTemplateParser("{{  hello   \t name=\"Dan\" \n color = 'blue' }}") should
          be(BeardTemplate(List(
            Interpolation(Identifier("hello"), Map("name" -> "Dan", "color" -> "blue"))
          )))
      }

      describe("attribute values") {

        it("should preserve white spaces, tabs and new lines inside of an attribute value") {
          BeardTemplateParser("{{hello name=\"D a\tn\" color = 'bl\nue'}}") should
            be(BeardTemplate(List(
              Interpolation(Identifier("hello"), Map("name" -> "D a\tn", "color" -> "bl\nue"))
            )))
        }

        it("should allow UTF-8 chars inside of attribute values") {
          BeardTemplateParser("{{hello name=\"å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆDan\"}}") should
            be(BeardTemplate(List(
              Interpolation(Identifier("hello"), Map("name" -> "å∂ßå∑œ´˚∆˙ø¨…˚¬∆˜≥≤µøˆDan"))
            )))
        }

        it("should allow special chars inside of attribute values except quotes and double quotes") {
          BeardTemplateParser("{{hello name=\"~!@#$%^&*()_+|-=\\<>,.?;:[]\"}}") should
            be(BeardTemplate(List(
              Interpolation(Identifier("hello"), Map("name" -> "~!@#$%^&*()_+|-=\\<>,.?;:[]"))
            )))
        }
      }

      describe("attribute identifiers") {

        it("should not start with a number") {
          BeardTemplateParser("{{9hello name=\"Dan\" color = 'blue'}}") should
            be(BeardTemplate(List(
              Interpolation(Identifier("hello"), Map("name" -> "Dan", "color" -> "blue"))
            )))
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
            Interpolation(Identifier("hello"), Map("name" -> "  He   llo  ", "color" -> "blue")),
            Text(" world"))))
      }
    }
  }
}
