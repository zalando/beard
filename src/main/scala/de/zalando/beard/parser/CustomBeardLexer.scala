package de.zalando.beard.parser

import de.zalando.beard.BeardLexer
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.{CharStream, LexerNoViableAltException}

class CustomBeardLexer(input: CharStream) extends BeardLexer(input) {

  override def notifyListeners(e: LexerNoViableAltException): Unit = {
    val sourceCode = extractSourceCode()
    val tokenText = _input.getText(Interval.of(_tokenStartCharIndex, _input.index()))
    val msg =
      s"""token recognition error at: '${getErrorDisplay(tokenText)}'
         |$sourceCode
      """.stripMargin

    val listener = getErrorListenerDispatch
    listener.syntaxError(this, null, _tokenStartLine, _tokenStartCharPositionInLine, msg, e)
  }

  private val DefaultStringLength = 80

  private def extractSourceCode(): String = {
    val lineStart = Math.max(0, _tokenStartCharIndex - _tokenStartCharPositionInLine)
    val lineEnd = Math.min(_input.size, _tokenStartCharIndex + DefaultStringLength)

    val lineOfCode = _input
      .getText(Interval.of(lineStart, lineEnd))
      .lines()
      .iterator()
      .next()

    val highlight = "^".padTo(_tokenStartCharPositionInLine + 1, " ").reverse.mkString

    val codeWithHighlights: String = Seq[String](lineOfCode, highlight).mkString(System.lineSeparator)
    codeWithHighlights
  }
}
