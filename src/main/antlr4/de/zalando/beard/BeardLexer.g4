lexer grammar BeardLexer;

LL : '{{' -> pushMode(INSIDE_INTERPOLATION);

NL      : '\r'? '\n';

WS      : [ \t];

TEXT    : ~('{' | '}' | ' ' | '\t' | '\n' | '\r')+;

CURLY_BRACKET : '{' | '}' ;

COMMENT: '{{#' (LL| NL | WS | RR | TEXT)* '#}}' -> skip;

mode INSIDE_INTERPOLATION;

    RR : '}}' -> popMode;

    SLASH: '/';

    COMMA: ',';
    
    BAR: '|';

    EXTENDS : 'extends';

    YIELD : 'yield';

    RENDER : 'render';

    BLOCK : 'block';

    CONTENT_FOR : 'contentFor';

    IF : 'if';
    ELSE: 'else';

    FOR: 'for';
    IN: 'in';

    IDENTIFIER
        : LETTER LETTER_OR_DIGIT*
        ;

    DOT : '.'
        ;

    START_ATTR
        : '='
        ;

    START_ATTR_VALUE: ["'] -> pushMode(INSIDE_ATTR_VALUE);

    fragment
    LETTER
        :    [a-zA-Z$_] // these are the "java letters" below 0xFF
        |    // covers all characters above 0xFF which are not a surrogate
            ~[\u0000-\u00FF\uD800-\uDBFF]
            {Character.isJavaIdentifierStart(_input.LA(-1))}?
        |    // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
            [\uD800-\uDBFF] [\uDC00-\uDFFF]
            {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
        ;

    fragment
    LETTER_OR_DIGIT
        :    [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
        |    // covers all characters above 0xFF which are not a surrogate
            ~[\u0000-\u00FF\uD800-\uDBFF]
            {Character.isJavaIdentifierPart(_input.LA(-1))}?
        |    // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
            [\uD800-\uDBFF] [\uDC00-\uDFFF]
            {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
        ;

    NLSKIP      :   '\r'? '\n' -> skip;

    WSSKIP      :   [ \t]+ -> skip ;

mode INSIDE_ATTR_VALUE;

    END_ATTR_VALUE: ["'] -> popMode;

    ATTR_TEXT : ~([{}"'])+;