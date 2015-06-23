parser grammar BeardParser;

options {
  tokenVocab=BeardLexer;
}

@parser::header{
import de.zalando.beard.ast.*;
}

beard
locals [scala.collection.immutable.List<Sentence> result]
      : sentence*
      ;

sentence
locals [Sentence result]
         : interpolation sentence
         | text sentence
         | text
         | interpolation
         ;

interpolation
locals [Interpolation result]
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