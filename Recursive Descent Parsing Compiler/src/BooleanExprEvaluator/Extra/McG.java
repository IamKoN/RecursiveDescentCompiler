package BooleanExprEvaluator.Extra;
public class McG {

Description: This program is a recursive descent compiler to
		parse and encode expressions from the following
		context-free grammar:

                	EXPR --> EXPR + TERM | EXPR - TERM
			TERM --> TERM * FCTR | TERM / FCTR | FCTR
			FCTR --> id | ( EXPR )

  The expressions will be encoded for a hypothetical stack machine with the
  following instructions:

		LOD  A    -- push the value in A on top of the stack
		ADD       -- add the two values on top of the stack, push result
		SUB       -- subtract     "
		MPY       -- multiply     "
		DIV       -- divide       "
*/

#include <stdio.h>  // standard i/o prototypes
#include <string.h> // string function prototypes
#include <ctype.h>  // char function prototypes

// constants
#define PLUSSYM         '+'
#define MINUSSYM        '-'
#define MULTSYM         '*'
#define DIVSYM          '/'
#define OPENPAREN       '('
#define CLOSEPAREN      ')'
#define BLANK           ' '
#define NULLSTRING	""

typedef int boolean;

// function prototypes
void open_data_file(int, char *[]);
void parse(void);
void expr(void);
void term(void);
void factor(void);
void error(char *);
void scan(void);

//necessary global variables
FILE *infile;
char lookahead[7];

void main(int argc, char *argv[])
{
  open_data_file(argc, argv);
  parse();
  puts("End of Compilation...");
}

void open_data_file(int argc, char *argv[])
{
  /* This function opens the data file containign the expression for this
	execution of the compiler. */
  // local variables
//  char filename[50];
  infile = NULL;
  if (argc > 1) {
    if ( (infile=fopen(argv[1],"r")) == NULL ) {
	 fprintf(stderr,"Error opening input file:  %s",argv[1]);
    }
  }
  else
    infile = stdin;

} // end open_data_file

void error(char *errstr)
{
  fprintf(stderr,"%s\n", errstr);
} // end error

void scan(void)
{
/* This procedure is a simple lexical analyzer; it produces the next token from
   the input string each time it is called */
   int ch; int i;


   strcpy(lookahead,NULLSTRING);
   while (BLANK == (ch = getc(stdin))) // Ignore any blanks
	{ /* do nothing */}
   switch (ch) {
	case PLUSSYM:
	case MINUSSYM:
	case MULTSYM:
	case DIVSYM:
	case OPENPAREN:
	case CLOSEPAREN:
	{ /* note: all operators are 1 character */
	   lookahead[0] = ch;
	   lookahead[1] = '\0';
	   break;
	}
	default:
	   { /* identifiers are any sequence of non-delimiters */
	   i = 0;
	   do {
		   lookahead[i++] = ch;
		   ch = getc(stdin);
	   } while ( (toupper(ch) >= 'A') && (toupper(ch) <= 'Z'));
	   lookahead[i] = '\0';
	   ungetc(ch,stdin);
	}
    } // end switch
} /*end scan()*/

void match(char *token){
    if (strcmp(token,lookahead) == 0)  /* then a match has been made*/
	 scan();          /* get new lookahead */
    else
	 error(strcat(token," expected"));     /* report "TOKEN expected" */
}  /* end match() */

boolean identifier(char *token)
{
  /* checks for a valid identifier -- a sequence of nondelimiters starting
    with a letter  */
   return ( ((token[0] >= 'A') && (token[0] <= 'Z')) ||
			   ((token[0] >= 'a') && (token[0] <= 'z')));
} /* end identifier() */

void emit(char * opcode)
{
   printf("%s\n",opcode);
} /* end emit() */

void fctr()
 /* This procedure handles the assembly of factors */
{
   char s[20] = NULLSTRING;

   /*First, check for an identifier*/
   if (identifier(lookahead)) {
	strcat(s,"LOD ");
	emit(strcat(s,lookahead));
	match(lookahead);
   }
   else { /* check for a parenthesized subexpression */
    if( lookahead[0] == OPENPAREN ) {
	 match("(");
	 expr();
	 match(")");
    }
    else
	 error("malformed factor error" );
   }
} /* end fctr() */

void term()
 /*This procedure handles the assembly of terms */
{
  char temp[20] = NULLSTRING;

   fctr(); /* A term must begin with a factor */
   /* Now, process any multiplying operator */
   while ( (lookahead[0] == MULTSYM) || (lookahead[0] == DIVSYM) ) {
	strcpy(temp,lookahead);
	match(lookahead);
	fctr();
	if (strcmp(temp,"*") == 0)
	 emit("MPY");
	else
	 emit("DIV");
   } // end while

} //  end term()

void expr()
{
 /* This procedure handles the assembly of expressions */
  char temp[20] = NULLSTRING;

   term();  /* An expression must begin with a term */
   /* Now, process any adding operators */
   while ((lookahead[0] == PLUSSYM) || (lookahead[0] == MINUSSYM)) {
	strcpy(temp,lookahead);
	match(lookahead);
	term();
	if(strcmp(temp,"+")==0)
	 emit("ADD");
	else
	 emit("SUB");
    } // end while
} // end expr()

void parse(void)
{
	scan();
	expr();
} // end parse()
}
