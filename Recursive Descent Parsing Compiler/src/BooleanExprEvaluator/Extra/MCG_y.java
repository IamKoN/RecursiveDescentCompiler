/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BooleanExprEvaluator.Extra;

/**
 *
 * @author kathrynanne
 */
public class MCG_y {
    
    %{
#include <stdio.h>
#include <ctype.h>
/* #include <malloc.h> */
#include <string.h>
#include <stdlib.h>

#define yycode printf

#define YYSTYPE char*
char * gen_label();

%}

%token NUM
%token ID
%token PLUS
%token STAR
%token LP
%token RP
%token ASGN
%token SEMICOLON
%token BEGINSYM
%token ENDSYM
%token IFSYM
%token THENSYM
%token ENDIF

%left  PLUS
%left  STAR

%%

prog:     BEGINSYM seq ENDSYM   { printf("End of Compilation\n"); return 0; }
    ;
seq:      seq SEMICOLON stmt
    | stmt
    ;
stmt:     /* nothing */
    | astmt
    | ifstmt
    ;
astmt:    ID { yycode("RVALUE \t%s\n", $1);} ASGN expr { yycode("STO\n");}
    ;
ifstmt:   IFSYM expr {$$ = strdup(gen_label()); yycode("GOFALSE %4s\n", $$); }
                    THENSYM seq ENDIF {yycode("LABEL \t%4s\n", $3); }
    ;
expr:   expr PLUS expr {yycode("ADD\n");}
    |   expr STAR expr {yycode("MUL\n");}
    |   LP expr RP
    |   NUM { yycode("PUSH \t%s\n", $1);}
    |   ID  { yycode("LVALUE\t%s\n", $1);}
    ;
%%
#include "lex.yy.c"

char *gen_label()
{
    static int i = 1000;
    char *temp = malloc(5);
    sprintf(temp,"%04d",i++);
    return temp;
}

yyerror(char *s)
{
    printf("%s\n", s);
}

main()
{
    yyparse();
}

    
}
