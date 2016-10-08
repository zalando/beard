parser grammar BeardParser;

options {
  tokenVocab=BeardLexer;
}

@parser::header{
import de.zalando.beard.ast.*;
}

beard
locals [scala.collection.immutable.List<Statement> result]
      : (extendsStatement NL?)? (NL* contentForStatement)* statement*
      ;

statement
locals [scala.collection.immutable.List<Statement> result]
         : structuredStatement
         | interpolation
         | newLine
         | white
         | text
         ;


leadingSpace
locals [NewLine result]
              :NL+ WS*;

extendsStatement
locals [ExtendsStatement result]
    : LL EXTENDS attrValue RR
    ;

structuredStatement
    : ifStatement
    | unlessStatement
    | forStatement
    | renderStatement
    | blockStatement
    | yieldStatement
    ;

yieldStatement
locals [YieldStatement result]
    : NL WS* LL YIELD RR
    | LL YIELD RR
    ;

blockStatement
locals [BlockStatement result]
    : NL WS* blockInterpolation statement* NL WS* endBlockInterpolation
    | blockInterpolation statement* endBlockInterpolation
    ;

// {{ block header }}
blockInterpolation
    : LL BLOCK identifier RR
    ;

// {{/ block }}
endBlockInterpolation
    : LL SLASH BLOCK RR
    ;

contentForStatement
locals [ContentForStatement result]
    : contentForInterpolation statement* NL WS* endContentForInterpolation NL?
    | contentForInterpolation statement* endContentForInterpolation NL?
    ;

// {{ contentFor header }}
contentForInterpolation
    : LL CONTENT_FOR identifier RR
    ;

// {{/ contentFor }}
endContentForInterpolation
    : LL SLASH CONTENT_FOR RR
    ;

// {{render "the-template" name="Dan" email=the.email.variable}}
renderStatement
locals [RenderStatement result]
    : NL WS* LL RENDER attrValue attribute* RR
    | LL RENDER attrValue attribute* RR
    ;

ifStatement
locals [scala.collection.immutable.List<Statement> result]
    : startingSpace+=leadingSpace ifInterpolation NL ifStatements+=statement+ ifSpace+=leadingSpace elseInterpolation NL elseStatements+=statement+ elseSpace+=leadingSpace endIfInterpolation NL? # IfElseStatement
    | ifInterpolation ifStatements+=statement+ elseInterpolation elseStatements+=statement+ endIfInterpolation # IfElseStatement
    | startingSpace+=leadingSpace ifInterpolation NL statement+ ifSpace+=leadingSpace endIfInterpolation NL? # IfOnlyStatement
    | ifInterpolation statement+ endIfInterpolation # IfOnlyStatement
    ;

ifInterpolation
    : LL IF compoundIdentifier RR
    ;

elseInterpolation
    : LL ELSE RR
    ;

endIfInterpolation
    : LL SLASH IF RR
    ;

unlessStatement
locals [scala.collection.immutable.List<Statement> result]
    : startingSpace+=leadingSpace unlessInterpolation NL unlessStatements+=statement+ unlessSpace+=leadingSpace elseInterpolation NL elseStatements+=statement+ elseSpace+=leadingSpace endUnlessInterpolation NL? # UnlessElseStatement
    | unlessInterpolation unlessStatements+=statement+ elseInterpolation elseStatements+=statement+ endUnlessInterpolation # UnlessElseStatement
    | startingSpace+=leadingSpace unlessInterpolation NL statement+ unlessSpace+=leadingSpace endUnlessInterpolation NL? # UnlessOnlyStatement
    | unlessInterpolation statement+ endUnlessInterpolation # UnlessOnlyStatement
    ;

unlessInterpolation
    : LL UNLESS compoundIdentifier RR
    ;

endUnlessInterpolation
    : LL SLASH UNLESS RR
    ;

forStatement
locals [scala.collection.immutable.List<Statement> result]
    : leadingSpace forInterpolation NL statement+ NL WS* endForInterpolation NL?
    | forInterpolation statement+ endForInterpolation
    ;

forInterpolation
    : LL FOR iter+=identifier (COMMA index+=identifier)? IN coll+=compoundIdentifier RR
    ;

endForInterpolation
    : LL SLASH FOR RR
    ;

interpolation
locals [Interpolation result]
    : idInterpolation
    | attrInterpolation
    ;

// {{address.street.number | lowercase}}
idInterpolation
locals [IdInterpolation result]
    : LL compoundIdentifier filter* RR
    ;

// | currency symbol=the.symbol.from.model fractionSize="2"
// | number format="0.0"
filter
locals [FilterNode result]
    : BAR identifier attribute*
    ;

// {{address street="Ferdinand" number="1" name=the.name}}
attrInterpolation
locals [AttrInterpolation result]
    : LL identifier attribute* RR
    ;

attribute
locals [Attribute result]
    : attributeWithValue
    | attributeWithIdentifier
    ;

// name="John"
attributeWithValue
locals [AttributeWithValue result]
    : identifier START_ATTR attrValue
    ;

// name = the.name.variable
attributeWithIdentifier
locals [AttributeWithIdentifier result]
    : identifier START_ATTR compoundIdentifier
    ;

attrValue
locals [String result]
    : START_ATTR_VALUE ATTR_TEXT END_ATTR_VALUE
    ;

compoundIdentifier
locals [CompoundIdentifier result]
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

identifier
locals [Identifier result]
    : IDENTIFIER
    ;

white
locals [White result]
    : WS+
    ;

newLine
locals [NewLine result]
    : NL+
    ;

text
locals [Text result]
    : TEXT
    | CURLY_BRACKET
    ;