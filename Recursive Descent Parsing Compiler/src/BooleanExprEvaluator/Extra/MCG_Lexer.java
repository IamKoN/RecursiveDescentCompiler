/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BooleanExprEvaluator.Extra;

public class MCG_Lexer {
    
%{
#include <stdio.h>
#include "y.tab.h"
%}
delim   [ \n\t]
ws  {delim}+
number  [0-9]+
ident   [a-z]+

%%
{ws}                { ; }   /* skip blanks and tabs */
{number}            { yylval = strdup(yytext); return NUM; }
{ident}             { yylval = strdup(yytext); return ID; }
"+"                 { return PLUS; }
"*"                 { return STAR; }
"("                 { return LP; }
")"                 { return RP; }
":="                { return ASGN; }
";"                 { return SEMICOLON; }
"IF"                { return IFSYM; }
"{"                 { return BEGINSYM; }
"}"                 { return ENDSYM; }
"THEN"              { return THENSYM; }
"ENDIF"             { return ENDIF; }
.                   { printf("\n lexical error\n"); }
%%

    
}
