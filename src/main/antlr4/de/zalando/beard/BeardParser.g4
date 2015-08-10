parser grammar BeardParser;

options {
  tokenVocab=BeardLexer;
}

@parser::header{
import de.zalando.beard.ast.*;
}

beard
locals [scala.collection.immutable.List<Statement> result]
      : statement*
      ;

statement
locals [Statement result]
         : structuredStatement
         | interpolation
         | text
         ;

structuredStatement
    : ifStatement
    | forStatement
    ;


ifStatement
locals [IfStatement result]
    : ifInterpolation statement+ endIfInterpolation # IfOnlyStatement
    | ifInterpolation ifStatements+=statement+ elseInterpolation elseStatements+=statement+ endIfInterpolation # IfElseStatement
    ;

ifInterpolation
    : LL IF RR
    ;

elseInterpolation
    : LL ELSE RR
    ;

endIfInterpolation
    : LL SLASH IF RR
    ;

forStatement
locals [ForStatement result]
    : forInterpolation statement+ endForInterpolation
    ;

forInterpolation
    : LL FOR iter+=identifier IN coll+=identifier RR
    ;

endForInterpolation
    : LL SLASH FOR RR
    ;

interpolation
locals [Interpolation result]
    : idInterpolation
    | attrInterpolation
    ;

// {{address.street.number}}
idInterpolation
locals [IdInterpolation result]
    : LL identifier (DOT identifier)* RR
    ;

// {{address street="Ferdinand" number="1"}}
attrInterpolation
locals [AttrInterpolation result]
    : LL identifier attribute* RR
    ;

attribute
locals [scala.Tuple2<String, String> result]
    : identifier START_ATTR attrValue
    ;

attrValue
locals [String result]
    : START_ATTR_VALUE ATTR_TEXT END_ATTR_VALUE
    ;

identifier
locals [Identifier result]
    : IDENTIFIER
    ;

text
locals [Text result]
    : TEXT
    ;