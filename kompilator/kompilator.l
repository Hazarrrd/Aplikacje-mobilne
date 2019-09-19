/**
Jan Sieradzki
236441
*/

%option noyywrap
%option yylineno

%{
#include "kompilator.tab.h"
#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <string>
using namespace std;
%}

%x quote

%%

(\[)			{BEGIN(quote);}
<quote>(\[)             { printf("Błąd : zagnieżdżenie komentarza.\n"); yyterminate(); }
<quote>(])		{BEGIN(INITIAL);}	
<quote>(\n|.) 
<quote><<EOF>> 		{ printf("Błąd : brak zamnknięcia komentarza.\n"); yyterminate(); }  				

(DECLARE) 		{return DECLARE; }
(IN) 			{return IN; }
(END) 			{return END; }

(IF)			{return IF; }
(THEN) 			{return THEN; }
(ENDIF) 		{return ENDIF; }
(ELSE)			{return ELSE; }

(WHILE) 		{return WHILE; }
(DO) 			{return DO; }
(ENDWHILE) 		{return ENDWHILE; }
(ENDDO) 		{return ENDDO; }

(FOR) 			{return FOR; }
(ENDFOR) 		{return ENDFOR; }
(FROM) 			{return FROM; }
(TO) 			{return TO; }
(DOWNTO) 		{return DOWNTO; }

(READ) 			{return READ; }
(WRITE) 		{return WRITE; }

(:=) 			{return ASSIGN; }
(<=)     		{ return LWE; }
(>=)     		{ return GRE; }
(<)     		{ return LW; }
(>)     		{ return GR; }
(!=)     		{ return NE; }
(=)     		{ return EQ; }

[-+()/*%:;]     	{ return *yytext; printf("%s", yytext); }

[_a-z]+[0-9] {cout << "ERROR near line : " << yylineno << " : " << "Wrong name of variable/array (try without number): '" << yytext[strlen(yytext)-1] << "'" << endl;  exit(1);}
[0-9][_a-z]+ {cout << "ERROR near line : " << yylineno << " : " << "Wrong name of variable/array (try without number): '" << yytext[0] << "'" << endl;  exit(1);}
[_a-z]+			{ yylval.str = (char *)strdup(yytext); return PIDENTIFIER;}	
([1-9][0-9]*)|[0] 	{ yylval.str = (char *)strdup(yytext); return NUM;}
\r|\r\n|\n				{ }
[[:space:]] 			{ }
.				{cout << "ERROR near line : " << yylineno << " : " << "Invalid Character: '" << yytext[0] << "'" << endl;  exit(1);}

%%



