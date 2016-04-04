package de.zalando.beard.parser

import de.zalando.beard.ast.BeardTemplate
import de.zalando.beard.{BeardLexer, BeardParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

class BeardTemplateParser {

  def parse(s: String): BeardTemplate = {
    // create a CharStream that reads from standard input
    val input = new ANTLRInputStream(s)

    // create a lexer that feeds off of input CharStream
    val lexer = new CustomBeardLexer(input)

    // create a buffer of tokens pulled from the lexer
    val tokens = new CommonTokenStream(lexer)

    // create a parser that feeds off the tokens buffer
    val parser = new BeardParser(tokens)

    val listener = new BeardTemplateListener

    parser.addParseListener(listener)
    parser.beard
    listener.result
  }
}

object BeardTemplateParser {
  def apply(beardTemplate: String): BeardTemplate = {
    new BeardTemplateParser().parse(beardTemplate)
  }
}
