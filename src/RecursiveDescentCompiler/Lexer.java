package RecursiveDescentCompiler;

import java.io.*;
import java.util.*;

class Lexer {
    
    // A utility function to check if the given character is operand
    public boolean IsOp(String str)
    {
        boolean asnOp = str.equals(":=");
        boolean lgcOp = str.equals("!") || str.equals("||") || str.equals("&&");
        boolean cmpOp = str.equals("<") || str.equals(">") || str.equals("<=") ||
                str.equals(">=") || str.equals("==");
        boolean addOp = str.equals("+") || str.equals("-");
        boolean mulOp = str.equals("mod") || str.equals("*") || str.equals("div");
        boolean expOp = str.equals("^");
        boolean prnOp = str.equals("(") || str.equals(")");
        boolean sepOp = str.equals(";") || str.equals(" ") || str.equals("\t") ||
                str.equals("begin") || str.equals("end");

        return asnOp || addOp || mulOp || expOp || prnOp || sepOp;
    }
    
    public boolean IsID (Token token)
    {
        char tokenChar = token.text.charAt(0);
        return ((tokenChar >= 'A' && tokenChar <= 'Z') || (tokenChar >= 'a' && tokenChar <= 'z'));
    }
     
    public TokenType FindOpType(String firstOperator) {
        
        TokenType type = TokenType.UNKNOWN;
        int precedence = -1;
        switch(firstOperator)
        {
            case ":=":
                type = TokenType.ASSIGNMENT;
                precedence = 0;
            break;
            case "+":
                type = TokenType.ADD;
                precedence = 3;
            break;
            case "-":
                type = TokenType.SUB;
                precedence = 3;
            break;
            case "*":
                type = TokenType.MPY;
                precedence = 4;
            break;
            case "div":
                type = TokenType.DIV;
                precedence = 4;
            break;
            case "mod":
                type = TokenType.MOD;
                precedence = 4;
            break;
            case "^":
                type = TokenType.POW;
                precedence = 5;
            break;
            case "(":
                type = TokenType.LEFT_PAREN;
                precedence = 7;
            break;
            case ")":
                type = TokenType.RIGHT_PAREN;
                precedence = 7;
            break;
            case ";":
                type = TokenType.SEMICOLON;
                precedence = 9;
            break;
            case "begin":
                type = TokenType.BEGIN;
                precedence = 9;
            break;
            case "end":
                type = TokenType.HALT;
                precedence = 9;
            break;
            case " ":
                type = TokenType.BLANK;
                precedence = 9;
            break;
            case "\t":
                type = TokenType.TAB;
                precedence = 9;
            break;
            default:
                type = TokenType.UNKNOWN;
                precedence = -1;
            break;
        }
        return type;
    }
    
    public Integer FindOpPrecedence(String firstOperator) {

        int precedence = -1;
        switch(firstOperator)
        {
            case ":=":
                precedence = 0;
            break;
            case "+":
                precedence = 3;
            break;
            case "-":
                precedence = 3;
            break;
            case "*":
                precedence = 4;
            break;
            case "div":
                precedence = 4;
            break;
            case "mod":
                precedence = 4;
            break;
            case "^":
                precedence = 5;
            break;
            case "(":
                precedence = 7;
            break;
            case ")":
                precedence = 7;
            break;
            case ";":
                precedence = 9;
            break;
            case "begin":
                precedence = 9;
            break;
            case "end":
                precedence = 9;
            break;
            case " ":
                precedence = 9;
            break;
            case "\t":
                precedence = 9;
            break;
            default:
                precedence = -1;
            break;
        }
        return precedence;
    }
    
    public List<Token> Tokenize(String source) {
        
        List<Token> tokenList = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        StringBuffer postfix = new StringBuffer(source.length());
        
        String token = "", test = "";        
        TokenizeState state = TokenizeState.DEFAULT;

        for (int i = 0; i < source.length(); i++)
        {
            char chr = source.charAt(i);
            String str = Character.toString(chr);
            /*
            int index;
            if ( source.toLowerCase().indexOf(str.toLowerCase()) != -1 ) {
                
            test += str;
            index = source.toLowerCase().indexOf(str.toLowerCase());
            if (index != -1){
                //str found at index
                int intIndex = source.indexOf();
      
                if(intIndex == - 1)
                {
                   System.out.println("Hello not found");
                }
                else
                {
                   System.out.println("Found Hello at index " + intIndex);
                }
            }
            if (isOperand(chr)) {
                postfix.append(chr);
            } else if (chr == '(') {
                stack.push(chr);
            } else if (chr == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek() != '(')
                    return null;
                else if(!stack.isEmpty())
                    stack.pop();
            }
            else if (IsOp(str)) {
                if (!stack.isEmpty() && getPrecedence(c) <= getPrecedence(stack.peek())) {
                    postfix.append(stack.pop());
                } stack.push(chr);
            }
            */       
            switch(state)
            {
                case DEFAULT:     
                    if (IsOp(str)) {
                        TokenType opType = FindOpType(str);
                        tokenList.add(new Token(str, opType));
                    }
                    else if (Character.isDigit(chr)) {
                        token += chr;
                        state = TokenizeState.NUMBER;
                    }
                    else if (Character.isLetter(chr)) {
                        token += chr;
                        state = TokenizeState.KEYWORD;
                    }
                    else {
                        token += chr;
                        state = TokenizeState.OPERATOR;
                    }
                break;                
                case NUMBER:
                    if (Character.isDigit(chr)) {
                        token += chr;
                    }
                    else {
                        tokenList.add(new Token(token, TokenType.NUMBER));
                        token = "";
                        state = TokenizeState.DEFAULT;
                        i--;
                    }                
                break;
                case KEYWORD:
                    if (Character.isLetterOrDigit(chr)) {
                        token += chr;
                    }
                    else if (IsOp(token)) {
                        TokenType opType = FindOpType(token);
                        tokenList.add(new Token(token, opType));
                        token = "";
                        state = TokenizeState.DEFAULT;
                        i--;
                    }
                    else {
                        tokenList.add(new Token(token, TokenType.ID));
                        token = "";
                        state = TokenizeState.DEFAULT;
                        i--;
                    }
                break;
                case OPERATOR:
                    if (chr == '=' || chr == 't') {
                        token += chr;
                    }
                    else if (IsOp(token)) {
                        TokenType opType = FindOpType(token);
                        tokenList.add(new Token(token, opType));
                        token = "";
                        state = TokenizeState.DEFAULT;
                        i--;
                    }
                    else {
                        tokenList.add(new Token(token,  TokenType.UNKNOWN));
                        token = "";
                        state = TokenizeState.DEFAULT;
                        i--;
                    }
                break;
            }
        }
        return tokenList;
    }
}