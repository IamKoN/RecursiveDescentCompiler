/*
Description: This program is a recursive descent compiler to parse and encode
expressions from the following context-free grammar:

EXPR --> EXPR + TERM | EXPR - TERM
TERM --> TERM * FCTR | TERM / FCTR | FCTR
FCTR --> id | ( EXPR )
    
<program>     begin <stmt_list> end
<stmt>    	<id> := <expr> | ε
<stmt_list>   <stmt_list> ; <stmt> | <stmt>
<expr>  	<expr> + <term> | <expr> - <term> |  <term>
<term>  	<term> * <factor> | <term> div <factor> | <term> mod <factor> |  <factor>
<factor>  	<primary> ^ <factor> | <primary>
<primary>  	<id> | <num> | ( <expr> )

  The expressions will be encoded for a hypothetical stack machine with the
  following instructions:

LOD  A    -- push the value in A on top of the stack
    
PUSH		v	-- push v (an integer constant) on the stack
RVALUE   	l	-- push the contents of variable l
LVALUE	l	-- push the address of the variable l
POP			-- throw away the top value on the stack
STO			-- the rvalue on top of the stack is place in the lvalue below it and both are popped
COPY			-- push a copy of the top value on the stack
ADD       		-- pop the top two values off the stack, add them, and push the result
SUB     		-- pop the top two values off the stack, subtract them, and push the result
MPY    		-- pop the top two values off the stack, multiply them, and push the result
DIV       		-- pop the top two values off the stack, divide them, and push the result
MOD     		-- pop the top two values off the stack, compute the modulus, and push the result
POW                   -- pop the top two values off the stack, compute the exponentiation operation, push result
HALT			-- stop execution
*/

package RDPC;
import RecursiveDescentCompiler.TokenType;
import java.io.*;
import java.util.*;
public class NewCompiler {
 /*   
    //Data and instruction memory size
    final int MAX_DATA = 65536;
    final int MAX_CODE = 65536;
    
    public static final int HALT    = 0; // stop execution
    public static final int PUSH    = 1; // push v (an integer constant) on the stack
    public static final int RVALUE  = 2; // push the contents of variable l
    public static final int LVALUE  = 3; // push the address of the variable l
    public static final int ASGN    = 4; //
    public static final int POP     = 5; // throw away the top value on the stack
    public static final int STO     = 6; // the rvalue on top of the stack is place in the lvalue below it and both are popped
    public static final int COPY    = 7; // push a copy of the top value on the stack
    public static final int ADD     = 8; // pop the top two values off the stack, add them, and push the result
    public static final int SUB     = 9; // pop the top two values off the stack, subtract them, and push the result
    public static final int MPY     = 10; // pop the top two values off the stack, multiply them, and push the result
    public static final int DIV     = 11; // pop the top two values off the stack, divide them, and push the result
    public static final int MOD     = 12; // pop the top two values off the stack, compute the modulus, and push the result
    public static final int POW     = 13; // pop the top two values off the stack, compute the exponentiation operation, push result
*/

//constants

public static final char PLUSSYM     = '+';
public static final char MINUSSYM    = '-';
public static final char MULTSYM     = '*';
public static final char DIVSYM      = '/';
public static final char MODSYM      = '%';
public static final char EXPOSYM     = '^';
public static final char OPENPAREN   = '(';
public static final char CLOSEPAREN  = ')';
public static final char BLANK       = ' ';
public static final String NULLSTRING  = "";

int typedef;
//globals

public byte[] lookahead = new byte[(7)]; //char lookahead[7];


public static void main(String[] args) throws IOException {
    // Stack Machine object
    StackMachineCompiler vm = new StackMachineCompiler();
    vm.readBinFile();   

    // output
    vm.printData(0,5);
    vm.printInstruction(0,16);
    //try {vm.readBinFile(args[0]); } catch (Exception e) {System.out.println("No argument provided.");}
    System.out.println("Starting Execution...");
    vm.execute();
    //parse();
    
    System.out.println("...Compilation Finished");  
}
public void readBinFile() throws IOException {
    //command line> java Vm myprog.bin...for Java, args[0] = “myprog.bin”.

    Scanner sc = new Scanner(System.in);
    System.out.print("Enter file name to open: ");
    String inFile = sc.nextLine();

    // Binary file input objects
    FileInputStream fstream = new FileInputStream(inFile);
    DataInputStream inputFile = new DataInputStream(fstream);
    System.out.println("Reading numbers from the file:");

    boolean endOfFile = false;
    int counter = 0;
    int number;

    // Retrieve binary number from file
    while (!endOfFile) {
        try {
            number = inputFile.readInt();

            String pad32 = "00000000000000000000000000000000";
            String bin = Integer.toBinaryString(number);
            String result = pad32 + bin;
            result = result.substring(result.length() - 32, result.length());
            String bits6 = result.substring(10,16);
            String bits16 = result.substring(16,32);
            NewCompiler.this.setInstruction(counter, Integer.parseInt(bits6,2), Integer.parseInt(bits16,2));

            //code[counter] = number;
            //counter++;
        }
        catch (EOFException e){ endOfFile = true; }
        counter++;
    }
    // Close the file
    inputFile.close();
    return;
}
public void setData(int loc, int val) {
        data[loc] = val;
}
public void setInstruction(int loc, int opcode, int operand) {
    code[loc] = opcode << 16 | operand;
}
public void setInstruction(int loc, int opcode) {
    StackMachineCompiler.this.setInstruction(loc, opcode, 0);
}
public void printInstruction(int start, int stop) {
    int instr;
    System.out.println("Instructions:\nLoc\tContents\tOpcode\tOperand");
    for(int loc = start; loc <= stop;loc++) {
        instr = code[loc];
        System.out.printf("%d\t%08x\t%d\t%d\n", loc, instr, (instr >> 16), (instr & 0xFFFF));
    }
    System.out.println();
}
public void printData(int start, int stop) {
    int value;
    System.out.println("Data:\nLoc\tvalue");
    for(int loc = start; loc <= stop;loc++) {
        value = data[loc];
        System.out.printf("%d\t%d\n", loc, value);
    }
    System.out.println();
}
    
    
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

boolean identifier(char *token) {
  /* checks for a valid identifier -- a sequence of nondelimiters starting
    with a letter  */
   return ( ((token[0] >= 'A') && (token[0] <= 'Z')) ||
			   ((token[0] >= 'a') && (token[0] <= 'z')));
} /* end identifier() */

void emit(char * opcode) {
   printf("%s\n",opcode);
} /* end emit() */


//~~~~Token Operation Methods~~~~
    public int Expo() {
        MatchAndEat(TokenType.POW);
        return Factor();
    }
    
    public int Multiply() {        
        MatchAndEat(TokenType.MPY);
        return Factor();
    }
    
    public int Divide() {
        MatchAndEat(TokenType.DIV);
        return Factor();
    }
   
    public int Mod() {
        MatchAndEat(TokenType.MOD);
        return Factor();
    }
    
    public int Add() {                
        MatchAndEat(TokenType.ADD);
        return Term();
    }
    
    public int Subtract() {
        MatchAndEat(TokenType.SUB);
        return Term();
    }
    
    public int Program() {
        String b = "begin";
        String e = "end";\
        int result = 0;
        if (CurrentToken().type == TokenType.BEGIN) {
            MatchAndEat(TokenType.BEGIN);
            result = stmtList();
            MatchAndEat(TokenType.HALT);
        }
    }
    public int Factor() {
        int result = 0;        
        if (CurrentToken().type == TokenType.LEFT_PAREN)
        {
            MatchAndEat(TokenType.LEFT_PAREN);
            result = ArithmeticExpression();
            MatchAndEat(TokenType.RIGHT_PAREN);
        }
        else if (CurrentToken().type == TokenType.NUMBER)
        {            
            result = new Integer(CurrentToken().text).intValue();
            MatchAndEat(TokenType.NUMBER);
        }
        return result;
    }
    
    public int Term() {        
        int result = Factor();
        while (CurrentToken().type == TokenType.MPY || 
                CurrentToken().type == TokenType.DIV)
        {
            switch(CurrentToken().type)
            {
                case MULTIPLY:
                    result = result * Multiply();
                    
                break;
                case DIVIDE:                    
                    result = result / Divide();                               
                break;                               
            }
        }        
        return result;
    }
    
    public int ArithmeticExpression() {        
        int result = Term();                
        while (CurrentToken().type == TokenType.ADD || CurrentToken().type == TokenType.SUB)
        {
            switch(CurrentToken().type)
            {
                case ADD:                
                    result = result + Add();                    
                break;
                case SUBTRACT:
                    result = result - Subtract();
                break;                               
            }
        }                
        return result;
    }

void primary() { /* This procedure handles the assembly of factors */
    char s[20] = NULLSTRING;

    /*First, check for an identifier*/
    if (identifier(lookahead)) {
         strcat(s,"LOD ");
         emit(strcat(s,lookahead));
         match(lookahead);
    }
    // check for a parenthesized subexpression
    else {
        if( lookahead[0] == OPENPAREN ) {
             match("(");
             expr();
             match(")");
        }
    else
	 error("malformed factor error" );
   }
} /* end primary() */

void term() {
 /*This procedure handles the assembly of terms */
  char temp[20] = NULLSTRING;

   primary(); /* A term must begin with a factor */
   /* Now, process any multiplying operator */
   while ( (lookahead[0] == MULTSYM) || (lookahead[0] == DIVSYM) ) {
	strcpy(temp,lookahead);
	match(lookahead);
	primary();
	if (strcmp(temp,"*") == 0)
	 emit("MPY");
	else
	 emit("DIV");
   } // end while

} //  end term()

void expr() {
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

void parse(void) {
	scan();
	expr();
} // end parse()
}
