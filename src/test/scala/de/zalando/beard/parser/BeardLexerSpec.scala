package de.zalando.beard.parser

import de.zalando.beard.BeardLexer
import org.antlr.v4.runtime.ANTLRInputStream
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConversions._

class BeardLexerSpec extends FunSpec with Matchers {

  describe("BeardLexer") {
    it("should parse the correct tokens") {
      val stream = new ANTLRInputStream("more {{   hello   \n name='  He   llo  '}}\n { world   }")
      val lexer = new BeardLexer(stream)
      val tokens = lexer.getAllTokens.map(token => (token.getText, lexer.getTokenNames.toList(token.getType))).toList
      val expected = List(
        ("more ", "TEXT"),
        ("{{", "'{{'"),
        ("hello", "IDENTIFIER"),
        ("name", "IDENTIFIER"),
        ("=", "'='"),
        ("'", "START_ATTR_VALUE"),
        ("  He   llo  ", "ATTR_TEXT"),
        ("'", "END_ATTR_VALUE"),
        ("}}", "'}}'"),
        ("\n", "NL"),
        (" ", "TEXT"),
        ("{", "CURLY_BRACKET"),
        (" world   ", "TEXT"),
        ("}", "CURLY_BRACKET"))
      tokens shouldBe expected
    }
  }
}
