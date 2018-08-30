package RecursiveDescentCompiler;

import java.io.*;
import java.util.*;

class Lexer {  
    public boolean IsOp(String str) {
        boolean asnOp = str.equals(":=");
        boolean addOp = str.equals("+") || str.equals("-");
        boolean mulOp = str.equals("mod") || str.equals("*") || str.equals("div");
        boolean expOp = str.equals("^");
        boolean prnOp = str.equals("(") || str.equals(")");
        boolean sepOp = str.equals(";") || str.equals(" ") || str.equals("\t") ||
                str.equals("begin") || str.equals("end");
        
        //boolean compOp = str == '<' || str == '>' || str == '=';
        //boolean lgicOp = str == '!' || str == '|' || str == '&';
        
        return asnOp || addOp || mulOp || expOp || prnOp || sepOp;
    }
    public TokenType FindOpType(String firstOperator) {
        TokenType type = TokenType.UNKNOWN;
        switch(firstOperator)
        {
            case "+":
                type = TokenType.ADD;
            break;
            case "-":
                type = TokenType.SUB;
            break;
            case "*":
                type = TokenType.MPY;
            break;
            case "div":
                type = TokenType.DIV;
            break;
            case "mod":
                type = TokenType.MOD;
            break;
            case "^":
                type = TokenType.POW;
            break;
            case ":=":
                type = TokenType.ASSIGNMENT;  
            break;
            case ";":
                type = TokenType.SEMICOLON;
            break;
            case "(":
                type = TokenType.LEFT_PAREN;
            break;
            case ")":
                type = TokenType.RIGHT_PAREN;
            break;
            case "begin":
                type = TokenType.BEGIN;
            break;
            case "end":
                type = TokenType.HALT;
            break;
            case " ":
                type = TokenType.BLANK;
            break;
            case "\t":
                type = TokenType.TAB;
            break;
            default:
                type = TokenType.UNKNOWN;
            break;
        }
        return type;
    }
    
// A utility function to check if the given character is operand

    public boolean IsID (Token token) {
        char tokenChar = token.text.charAt(0);
        return ((tokenChar >= 'A' && tokenChar <= 'Z') || (tokenChar >= 'a' && tokenChar <= 'z'));
    }
    public int getPrecedence(char ch) {
        switch (ch) {
        case '+':
        case '-':
            return 1;

        case '*':
        case '/':
            return 2;

        case '^':
            return 3;
        }
        return -1;
    }
    
    public List<Token> Tokenize(String source) {
        List<Token> tokenList = new ArrayList<Token>();
        
        Stack<Token> stack = new Stack<Token>();
        StringBuffer postfix = new StringBuffer(source.length());
        
        String token = "", test = "";        
        TokenizeState state = TokenizeState.DEFAULT;
        int index;
        for (int i = 0; i < source.length(); i++)
        {
            char chr = source.charAt(i);
            String str = Character.toString(chr);
            /*test += str;
            index = source.toLowerCase().indexOf(str.toLowerCase());
            if (index != -1){
                str found at index
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
            }*/       
            
            switch(state)
            {
                case DEFAULT:              
                    //TokenType opType = FindOpType(str);                    
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