package de.zalando.beard.parser

import org.antlr.v4.runtime.ANTLRInputStream
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

class BeardLexerSpec extends FunSpec with Matchers {

  describe("BeardLexer") {
    it("should parse the correct tokens") {
      val stream = new ANTLRInputStream("more {{   hello   \n name='  He   llo  '}} { world   } {{ val | filter }}")
      val lexer = new CustomBeardLexer(stream)
      val tokens = lexer.getAllTokens.asScala.map(token => (token.getText, lexer.getTokenNames.toList(token.getType))).toList
      val expected = List(
        ("more", "TEXT"),
        (" ", "WS"),
        ("{{", "'{{'"),
        ("hello", "IDENTIFIER"),
        ("name", "IDENTIFIER"),
        ("=", "'='"),
        ("'", "START_ATTR_VALUE"),
        ("  He   llo  ", "ATTR_TEXT"),
        ("'", "END_ATTR_VALUE"),
        ("}}", "'}}'"),
        (" ", "WS"),
        ("{", "CURLY_BRACKET"),
        (" ", "WS"),
        ("world", "TEXT"),
        (" ", "WS"),
        (" ", "WS"),
        (" ", "WS"),
        ("}", "CURLY_BRACKET"),
        (" ", "WS"),
        ("{{", "'{{'"),
        ("val", "IDENTIFIER"),
        ("|", "'|'"),
        ("filter", "IDENTIFIER"),
        ("}}", "'}}'"))
      tokens shouldBe expected
    }
  }
}
