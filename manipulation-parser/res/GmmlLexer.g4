// ============================================================================
// Braintribe IT-Technologies GmbH - www.braintribe.com
// Copyright Braintribe-IT Technologies GmbH, Austria, 2002-2015 - All Rights Reserved
// It is strictly forbidden to copy, modify, distribute or use this code without written permission
// To this file the Braintribe License Agreement applies.
// ============================================================================
lexer grammar GmmlLexer;

@header {
package com.braintribe.model.processing.manipulation.parser.impl.autogenerated;
}

// most specific rules first, then general rules afterwards


// --------------------------Keywords-------------------

Boolean:     'true'	| 'false';
Null:        'null';
Colon:       ':';
Colon_Colon: '::';

//--------------------------Manipulation Operators------------

EQ:          '=';
PLUS:        '+';
MINUS:       '-';
MINUS_MINUS: '--';

//---------------------------Symbols-------------------

LB:          '(';
RB:          ')';
LSB:         '[';
RSB:         ']';
LCB:         '{';
RCB:         '}';
COMMA:       ',';
Dot:         '.';
EXCLAMATION: '!';

//----------------------------Identifier-----------------

StandardIdentifier: IdentifierFirstCharacter (IdentifierCharacter)*;

fragment IdentifierFirstCharacter:
	Char
	| UnderScore
	| DollarSign
;

fragment IdentifierCharacter:
	IdentifierFirstCharacter
	| Digit
; 

fragment Char:
	'a' .. 'z'
	| 'A' .. 'Z'
;

fragment UnderScore: '_';
fragment DollarSign: '$';

//----------------------------Date Literal---------------

DateFunction: 'date' LB;

DateOffset:
	YearFragment
	| MonthFragment
	| DayFragment
	| HourFragment
	| MinuteFragment
	| SecondFragment
	| MilliSecondFragment
;

TimeZoneOffset: ZoneFragment;

fragment YearFragment:        Digit+ 'Y';
fragment MonthFragment:       Digit+ 'M';
fragment DayFragment:         Digit+ 'D';
fragment HourFragment:        Digit+ 'H';
fragment MinuteFragment:      Digit+ 'm'; // check the details
fragment SecondFragment:      Digit+ 'S';
fragment MilliSecondFragment: Digit+ 's';
fragment ZoneFragment:        PlusOrMinus? Digit Digit Digit Digit 'Z'; // format always hhmm with a + or - before it

//----------------------------Base Literals--------------

DecimalLiteral:	(FloatingPointLiteral | IntegerBase10Literal) DecimalSuffix;
FloatLiteral:   (FloatingPointLiteral | IntegerBase10Literal | FloatingPointSpecial) FloatSuffix;

DoubleLiteral: 
	FloatingPointLiteral DoubleSuffix?
	| IntegerBase10Literal DoubleSuffix
	| FloatingPointSpecial DoubleSuffix
;

fragment FloatingPointSpecial: '+NaN' | PlusOrMinus 'Infinity';

fragment FloatingPointLiteral:
	PlusOrMinus?
	(
		Digit+ Dot Digit* Exponent?
		| Dot Digit+ Exponent?
		| Digit+ Exponent
	)
;

fragment Exponent:	ExponentIndicator PlusOrMinus? Digit+;

LongBase10Literal:    IntegerBase10Literal LongSuffix;
LongBase16Literal:    IntegerBase16Literal LongSuffix;

IntegerBase10Literal: PlusOrMinus? Digit+;
IntegerBase16Literal: PlusOrMinus? ZeroDigit HexInfix HexDigit+;

fragment HexDigit:
	Digit
	| 'a' .. 'f'
	| 'A' .. 'F'
;

fragment Digit:             ZeroDigit | PositiveDigit;
fragment ZeroDigit:         '0';
fragment PositiveDigit:	    '1' .. '9';

fragment PlusOrMinus:       '+' | '-';

fragment LongSuffix:        'l' | 'L';
fragment FloatSuffix:       'f' | 'F';
fragment DoubleSuffix:      'd' | 'D';
fragment DecimalSuffix:     'b'	| 'B';
fragment ExponentIndicator: 'e'	| 'E';

fragment HexInfix:          'x' | 'X';

StringOpen: SingleQuote -> pushMode ( InsideString );
fragment SingleQuote: '\'';

//----------------------------White space----------------

WS:	[ \t\f\r\n]+ -> skip; // skip spaces, tabs, newlines

// Comment are the same as in Java

Comment : '/*' .*? '*/' -> skip;

LineComment:   '//' ~[\r\n]* -> skip;

ANY: . ;

//----------------------------String Mode----------------
mode InsideString;

fragment BackSlash: '\\';

UnicodeEscape: BackSlash 'u' HexDigit HexDigit HexDigit HexDigit;
EscB: BackSlash 'b';
EscT: BackSlash 't';
EscN: BackSlash 'n';
EscF: BackSlash 'f';
EscR: BackSlash 'r';
EscSQ: BackSlash '\'';
EscBS: BackSlash BackSlash;

PlainContent:
	(
		~( '\'' | '\\' )
	)+
;

StringClose: SingleQuote -> popMode;
