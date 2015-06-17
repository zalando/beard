package de.zalando.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class TheBeard {

    public String parse(String s) {
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(s);

        // create a lexer that feeds off of input CharStream
        BeardLexer lexer = new BeardLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        BeardParser parser = new BeardParser(tokens);

        ParseTree tree = parser.beard(); // begin parsing at beard rule
        return tree.toStringTree(parser); // print LISP-style tree
    }
}
