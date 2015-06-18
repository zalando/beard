package de.zalando.beard

import de.zalando.beard.ast.BeardTemplate
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.{CommonTokenStream, ANTLRInputStream}

class BeardTemplateParser {

  def parse(s: String): BeardTemplate = {
    // create a CharStream that reads from standard input
    val input = new ANTLRInputStream(s)

    // create a lexer that feeds off of input CharStream
    val lexer = new BeardLexer(input)

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
