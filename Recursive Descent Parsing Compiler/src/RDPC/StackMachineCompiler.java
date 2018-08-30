package RDPC;

import java.io.*;
import java.util.*;
public class StackMachineCompiler {

    //Data and instruction memory size
    final int MAX_DATA = 65536;
    final int MAX_CODE = 65536;
    
    //Opcodes
    final int HALT = 0;
    final int PUSH = 1;
    final int RVALUE = 2;
    final int LVALUE = 3;
    final int POP = 4;
    final int STO = 5;
    final int COPY = 6;
    final int ADD = 7;
    final int SUB = 8;
    final int MPY = 9;
    final int DIV = 10;
    final int MOD = 11;
    final int NEG = 12;
    final int NOT = 13;
    final int OR = 14;
    final int AND = 15;
    final int EQ = 16;
    final int NE = 17;
    final int GT = 18;
    final int GE = 19;
    final int LT = 20;
    final int LE = 21;
    final int LABEL = 22;
    final int GOTO = 23;
    final int GOFALSE = 24;
    final int GOTRUE = 25;
    final int PRINT = 26;
    final int READ = 27;
    final int GOSUB = 28;
    final int RET = 29;
    final int ORB = 30;
    final int ANDB = 31;
    final int XORB = 32;
    final int SHL = 33;
    final int SHR = 34;
    final int SAR = 35;
    
    
    public static String [] opcodes = {"HALT","PUSH","RVALUE","LVALUE",
            "MOD","DIV","MPY","EXPO","ADD","SUB","COPY","STO","POP",":="};
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
    
    // constructor initializes the stack machine
    public StackMachineCompiler() {
        // Set mem sizes to max value (32 bits)
        code = new int[MAX_CODE];
        data = new int[MAX_DATA];
        PC = 0;
        run = true;
        stack = new Stack<Integer>();
        callStack = new Stack<Integer>();
        
        // Initialize values in segment .data to manipulate
        data[0] = 6;                                        
        data[1] = 2;
        data[2] = 14;
        data[3] = 12;
        data[4] = 8;
        data[5] = 4;
    }  
    public void execute() {
        while(run) {
            getNextInstruction();
            decodeAndRunInstruction();
        }
    }
    public void getNextInstruction() {
            IR = code[PC++];
    }
    public void decodeAndRunInstruction() {
        // pull out the opcode and the operands
        int opcode = IR >> 16;                  
        int operand = IR & 0xFFFF;
        // First register at bottom of the stack
        int op1;                               
        // Secondd register at top of stack
        int op2;
        
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
            case NEG:
                op1 = stack.pop();
                op1 = 0 - op1;
                stack.push(op1);
                break;
            case NOT:
                op1 = stack.pop();
                op1 = ~op1;
                stack.push(op1);
                break;
            case OR:
                op2 = stack.pop();
                op1 = stack.pop();
                if (op1 != 0 || op2 != 0)
                    stack.push(1);
                else
                    stack.push(0);
                break;
            case AND:
                op2 = stack.pop();
                op1 = stack.pop();
                if (op1 != 0 && op2 != 0)
                    stack.push(1);
                else
                    stack.push(0);
                break;
            case EQ:
                op2 = stack.pop();
                op1 = stack.pop();
                if(op1 == op2)
                    stack.push(1);
                else
                    stack.push(0);
                break;
            case NE:
                op2 = stack.pop();
                op1 = stack.pop();
                if(op1 != op2)
                    stack.push(1);
                else
                    stack.push(0);
                break;
            case GT:
                op2 = stack.pop();
                op1 = stack.pop();
                if(op1 > op2)
                    stack.push(1);
                else
                    stack.push(0);
                break;
            case GE:
                op2 = stack.pop();
                op1 = stack.pop();
                if(op1 >= op2)
                    stack.push(1);
                else
                    stack.push(0);
                break;
            case LT:
                op2 = stack.pop();
                op1 = stack.pop();
                if(op1 < op2)
                    stack.push(1);
                else
                    stack.push(0);
                break;
            case LE:
                op2 = stack.pop();
                op1 = stack.pop();
                if(op1 <= op2)
                    stack.push(1);
                else
                    stack.push(0);
                break;
            case LABEL:
                break;
            case GOTO:
                PC = operand;
                break;
            case GOFALSE:                           // Update PC to jump location
                op1 = stack.pop();
                if (op1 == 0) {PC = operand;}   
                break;
            case GOTRUE:                            // Update PC to jump location
                op1 = stack.pop();
                if (op1 != 0) {PC = operand;}   
                break;
            case PRINT:                             // pop value off top of stack and print to terminal
                op1 = stack.pop();
                System.out.println("Printing : " + op1);
                stack.push(op1);
                break;
            case READ:
                Scanner keyboard = new Scanner(System.in);
                int input = keyboard.nextInt();
                stack.push(input);
                break;
            case GOSUB:                         // push PC+1 onto call stack, jump to subroutine address in operand 
                callStack.push(PC +1);
                PC = operand;
                break;
            case RET:
                PC = callStack.pop();
                break;
            case ORB:                           //compute the bitwise OR and push the result
                op2 = stack.pop();
                op1 = stack.pop();
                op1 = (op1 | op2);
                stack.push(op1);
                break;
            case ANDB:                          //compute the bitwise AND and push the result
                op2 = stack.pop();
                op1 = stack.pop();
                op1 = (op1 & op2);
                stack.push(op1);
                break;
            case XORB:                          //compute the bitwise XOR and push the result
                op2 = stack.pop();
                op1 = stack.pop();
                op1 = op1 ^ op2;
                stack.push(op1);
                break;
            case SHL:                           //logical shift bits left by 1 bit, and push the result
                op1 = stack.pop();
                op1 = op1 << 1;
                stack.push(op1);
                break;
            case SHR:                           //logical shift bits right by 1 bit, and push the result
                op1 = stack.pop();
                op1 = op1 >> 1;
                stack.push(op1);
                break;
            case SAR:                           //arithmetic shift the bits right by 1 and push result
                op1 = stack.pop();
                op1 = op1 >>> 1;
                stack.push(op1);
            
            default:
                //System.err.println("Unimplemented opcode");
                System.err.println("Unemplemented opcode" + opcode + "ln");
                System.exit(opcode);
        }
        if (IR == 0) run = false;               // fail safe for HALT. Terminates program
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
                StackMachineCompiler.this.setInstruction(counter, Integer.parseInt(bits6,2), Integer.parseInt(bits16,2));
                
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
        System.out.println("...Finished");  
    }
}
