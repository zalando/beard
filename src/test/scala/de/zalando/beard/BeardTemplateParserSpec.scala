package de.zalando.beard

import de.zalando.beard.ast.{Identifier, Interpolation, BeardTemplate, Text}
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
  }
}
