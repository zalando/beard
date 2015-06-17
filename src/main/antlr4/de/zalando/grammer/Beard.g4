grammar Beard;


beard : template*
      ;

template : interpolation template
         | text template
         | text
         | interpolation
         ;

interpolation : LL identifier RR
              ;

identifier: TEXT;

text: TEXT;

// parser rules start with lowercase letters, lexer rules with uppercase


LL : '{{';
RR : '}}';

TEXT : ('A'..'Z' | 'a'..'z' | [' \t'] | [\r\n] | [<>="/])+;
