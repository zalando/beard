grammar Beard;

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
    : LL identifier RR
    ;

identifier
locals [Identifier result]
    : TEXT
    ;

text
locals [Text result]
    :TEXT
    ;

// parser rules start with lowercase letters, lexer rules with uppercase


LL : '{{';
RR : '}}';

TEXT : ~([{}])+;
