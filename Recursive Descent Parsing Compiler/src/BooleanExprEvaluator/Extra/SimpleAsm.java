/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BooleanExprEvaluator.Extra;

import java.util.*;
import java.io.*;

public class SimpleAsm
{
	public static final int HALT 	= 0; // stop execution
	public static final int PUSH 	= 1; // push v (an integer constant) on the stack
	public static final int RVALUE 	= 2; // push the contents of variable l
	public static final int LVALUE 	= 3; // push the address of the variable l
	public static final int ASGN 	= 4; //
        public static final int POP	= 5; //	throw away the top value on the stack
        public static final int STO	= 6; //	the rvalue on top of the stack is place in the lvalue below it and both are popped
        public static final int COPY	= 7; //	push a copy of the top value on the stack
        public static final int ADD     = 8; // pop the top two values off the stack, add them, and push the result
        public static final int SUB     = 9; // pop the top two values off the stack, subtract them, and push the result
        public static final int MPY     = 10; // pop the top two values off the stack, multiply them, and push the result
        public static final int DIV     = 11; // pop the top two values off the stack, divide them, and push the result
        public static final int MOD     = 12; // pop the top two values off the stack, compute the modulus, and push the result
        public static final int POW     = 13; // pop the top two values off the stack, compute the exponentiation operation, push result

            // reference for the memory modules
    int[] code;                                             
    int[] data;
    // Program Counter (holds current instruction address)
    int PC;                                                 
    // Instruction Reader (holds instruction at current PC)
    int IR;
    // Boolean for starting/stopping VM
    boolean run;
    // data stack
    Stack<Integer> stack;    
    // call stack
    Stack<Integer> callStack;
        // pull out the opcode and the operands
        int opcode = IR >> 16;                  
        int operand = IR & 0xFFFF;
        // First register at bottom of the stack, Secondd register at top of stack
        int op1, op2;
        
        switch(opcode) {
            case HALT:
                run = false;
                break;
            case PUSH:                      // push a literal value
                stack.push(operand);
                break;
            case RVALUE:                    // push the contents of a memory address
                stack.push(data[operand]);
                break;
            case LVALUE:                    // push the address/literal
                stack.push(operand);
                break;
            case POP:                       // Throw away the value on top of the stack
                stack.pop();
                break;
            case STO:
                int rvalue = stack.pop();
                int lvalue = stack.pop();
                data[lvalue] = rvalue;
                break;
            case COPY:                      // Push a copy of the top value on the stack ***MAY NEED TO BE CHANGED***
                op1 = stack.peek();
                stack.push(op1);
                break;
            case ADD:
                op2 = stack.pop();
                op1 = stack.pop();
                stack.push(op1 + op2);
                break;
            case SUB:
                op2 = stack.pop();
                op1 = stack.pop();
                stack.push(op1 - op2);
                break;
            case MPY:
                op2 = stack.pop();
                op1 = stack.pop();
                stack.push(op1 * op2);
                break;
            case DIV:
                op2 = stack.pop();
                op1 = stack.pop();
                stack.push(op1 / op2);
                break;
            case MOD:
                op2 = stack.pop();
                op1 = stack.pop();
                stack.push(op1 % op2);
                break;
            
            case PRINT:                 // pop value off top of stack and print to terminal
                op1 = stack.pop();
                System.out.println("Printing : " + op1);
                stack.push(op1);
                break;
            case READ:
                Scanner keyboard = new Scanner(System.in);
                int input = keyboard.nextInt();
                stack.push(input);
                break;
                       
            default:
                System.err.println("Unemplemented opcode" + opcode + "ln");
                System.exit(opcode);
        }
        if (IR == 0) run = false; 


	public static String [] opcodes = {"HALT","PUSH","RVALUE","LVALUE",
            "MOD","DIV","MPY","ADD","SUB","COPY","STO","POP",":="};

	public static void main(String [] args)throws IOException
	{
		// get filename
		String filename;
		SymbolTable symTab = new SymbolTable();

		if (args.length != 0)
		{
			filename = args[0];
		}
		else
		{
			filename = "simple.asm";
		}

		// Open file for input
		Scanner infile = new Scanner(new File(filename));

		// pass 1 -- build symbol table
		pass1(infile, symTab);
		infile.close();

		// pass 2 -- assemble
		// reopen source file
		infile = new Scanner(new File(filename));

		// pass 2 -- output binary code
		pass2(infile,symTab);
		infile.close();

		// print symbol table
		dumpSymbolTable(symTab);
		System.out.println("Done");
	}

	public static int lookUpOpcode(String s)
	{
		for(int i = 0; i < opcodes.length; i++)
		{
			if (s.equalsIgnoreCase(opcodes[i]))
			{
				return i;
			}
		}
		System.err.println("Invalid opcode:" + s);
		return -1;
	}

	public static void pass1(Scanner infile, SymbolTable tab)
	{
		// initialize location counter, etc.
		int locationCounter = 0;
		String line;
		Scanner input;
		String lexeme;

		// find start of data section
		do
		{
			line = infile.nextLine();
			System.out.println(line);
			input = new Scanner(line);
		} while (!input.next().equalsIgnoreCase("Section"));
		if (!input.next().equalsIgnoreCase(".data"))
		{
			System.err.println("Error:  Missing 'Section .data' directive");
			System.exit(1);
		}
		else
		{
			System.out.println("Parsing data section, pass 1");
		}

		// build symbol table from variable declarations
		line = infile.nextLine();
		input = new Scanner(line);

		// data section ends where code section begins
		while(!(lexeme = input.next()).equalsIgnoreCase("Section"))
		{
			// look for labels (they end with a colon)
			int pos = lexeme.indexOf(':');
			if (pos > 0)
			{
				lexeme = lexeme.substring(0,pos);
			}
			else
			{
				System.err.println("error parsing " + line);
			}
			// insert the lexeme, the type, and its address into the symbol table
			tab.insert(lexeme,"Int",locationCounter);
			locationCounter++;
			line = infile.nextLine();
			input = new Scanner(line);
		}

		// Now, parse the code section, looking for the label directive
		System.out.println("Parsing code section, pass 1");
		locationCounter=0;
		while(infile.hasNext())
		{
			line = infile.nextLine();
			input = new Scanner(line);
			lexeme = input.next();
			// when a labe is found, place it and it's code offset in the symbol table
			if (lexeme.equalsIgnoreCase("label"))
			{
				lexeme = input.next();
				tab.insert(lexeme,"Code",locationCounter);
			}
			locationCounter++;
		}
	}

	// generate the code
	public static void pass2(Scanner infile, SymbolTable tab)
	{
		// initialize location counter, etc.
		int locationCounter = 0;
		String line;
		Scanner input;
		String lexeme;
		int symTabPtr;
		SymbolTableEntry entry;
		final int NULL = -1;
		// find start of next section
		do
		{
			line = infile.nextLine();
			input = new Scanner(line);

		} while (!input.next().equalsIgnoreCase("Section"));
		if (!input.next().equalsIgnoreCase(".data"))
		{
			System.err.println("Error:  Missing 'Section .data' directive");
			System.exit(1);
		}
		else
		{
			System.out.println("Parsing data section, pass 2");
		}
		line = infile.nextLine();
		input = new Scanner(line);

		while(!(lexeme = input.next()).equalsIgnoreCase("Section"))
		{
			// data section has been processed in previous pass, so skip this
			line = infile.nextLine();
			input = new Scanner(line);
		}

		// Now, let's generate some code
		System.out.println("Parsing code section, pass 2");
		locationCounter=0;
		// while not end of file keep parsing
		while(infile.hasNext())
		{
			line = infile.nextLine();
			input = new Scanner(line);
			lexeme = input.next();
			int ptr;
			//	lookup opcode and generate appropriate instructions
			int opcode = lookUpOpcode(lexeme);
			switch(opcode)
			{
				case HALT:
					insertCode(locationCounter, HALT);
					break;
				case PUSH:
					lexeme = input.next();
					insertCode(locationCounter, PUSH, Integer.parseInt(lexeme));
					break;
				case RVALUE:
					lexeme = input.next();
					ptr = tab.lookup(lexeme);
					insertCode(locationCounter, RVALUE, tab.get(ptr).getAddress());
					break;
				case LVALUE:
					lexeme = input.next();
					ptr = tab.lookup(lexeme);
					insertCode(locationCounter, LVALUE, tab.get(ptr).getAddress());
					break;
				case ASGN:
					insertCode(locationCounter, ASGN);
					break;
				default:
					System.err.println("Unimplemented opcode:  " + opcode);
					System.exit(opcode);
			}
			locationCounter++;
		}
	}

	public static void insertCode(int loc, int opcode, int operand)
	{
		System.out.print(loc + ":\t" + opcode + "\t" + operand);;
	}

	public static void insertCode(int loc, int opcode)
	{
		insertCode(loc,opcode,0);
	}

	public static void dumpSymbolTable(SymbolTable tab)
	{
		System.out.println("\nlexeme \ttype \taddress");
		System.out.println("-----------------------------------------");
		for(int i=0; i<tab.size(); i++)
		{
			System.out.println(tab.get(i));
		}
	}
}
