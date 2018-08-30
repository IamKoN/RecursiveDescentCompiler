package RecursiveDescentCompiler;


import java.lang.*;
import java.io.*;
import java.util.*;

class Parser {       
    public int currentTokenPosition = 0;
    public List<Token> tokenList;            
    public Parser() {}    
    public Parser(List<Token> tokenList)
    {
        this.tokenList = tokenList;
    }
    public List<Token> GetTokenList()
    {
        return tokenList;
    }
   
//Token Manipulation Methods
    public Token GetToken(int offset) {
        if (currentTokenPosition + offset >= tokenList.size()) {
            return new Token("", TokenType.EOF);
        }
        return tokenList.get(currentTokenPosition + offset);
    }        
    public Token CurrentToken() {
        return GetToken(0);
    }
    public Token NextToken() {
        return GetToken(1);
    }
    public Token Match(TokenType type) {       
        Token token = CurrentToken();
                
        if (CurrentToken().type != type)
            System.out.println("Saw " + token.type + " but " + type + " expected");
        
        EatToken(1);
        return token;
    }
    public void EatToken(int offset) {           
        currentTokenPosition = currentTokenPosition + offset;
    }
    

    public int Push() {
        Match(TokenType.PUSH);
        return 0;
    }
    public int Pop() {
        Match(TokenType.PUSH);
        return 0;
    }
    public int Rvalue() {
        Match(TokenType.PUSH);
        return 0;
    }
    public int Lvalue() {
        Match(TokenType.PUSH);
        return 0;
    }
    public int Copy() {
        Match(TokenType.PUSH);
        return 0;
    }
    public int Sto() {
        Match(TokenType.PUSH);
        return 0;
    }

//Token Operation Methods  
    public int Expo() {
        Match(TokenType.POW);
        return Factor();
    }
    public int Multiply() {        
        Match(TokenType.MPY);
        return Term();
    }
    public int Divide() {
        Match(TokenType.DIV);
        return Term();
    }
    public int Mod() {
        Match(TokenType.MOD);
        return Term();
    }
    public int Add() {                
        Match(TokenType.ADD);
        return Expr();
    }
    public int Subtract() {
        Match(TokenType.SUB);
        return Expr();
    }
    public int Assign() {
        Match(TokenType.ASSIGNMENT);
        return Expr();
    }

//Production Grammar
    // <program>     begin <stmt_list> end
    public int Program() {
        int result = 0;
        if (CurrentToken().type == TokenType.BEGIN) {
            Match(TokenType.BEGIN);
            result = StmtList();
            Match(TokenType.HALT);
        }
        else
            Error("! malformed program error !");
        return result;
    }
    //<stmt_list>   <stmt_list> ; <stmt> | <stmt>
    public int StmtList() {
        int result = Stmt();
        if (CurrentToken().type == TokenType.SEMICOLON) {
            result = StmtList();
            Match(TokenType.SEMICOLON);
        }
        else
            Error("! malformed stmtlist error !");
        return result;
    } 
    //<stmt>  	<id> := <expr> | ε
    public int Stmt() {
        int result = Expr();
        if (CurrentToken().type == TokenType.ID) {
            Match(TokenType.ID);
            result = Assign();
        }
        else
            Error("! malformed stmt error !");
        return result;
    }
    //<expr>  	<expr> + <term> | <expr> - <term> |  <term>
    public int Expr() {        
        int result = Term();
        if (CurrentToken().type == TokenType.ADD || CurrentToken().type == TokenType.SUB) {
            while (CurrentToken().type == TokenType.ADD || CurrentToken().type == TokenType.SUB) {
                switch(CurrentToken().type) {
                    case ADD:                
                        result = Add() + result;                    
                    break;
                    case SUB:
                        result = Subtract() - result;
                    break;                               
                }
            }
        }
        else
            Error("! malformed expr error !");
        return result;
    }
    //<term>  	<term> * <factor> | <term> div <factor> | <term> mod <factor> |  <factor>
    public int Term() {    
        int result = Factor();
        if (CurrentToken().type == TokenType.MPY || 
                    CurrentToken().type == TokenType.DIV || 
                    CurrentToken().type == TokenType.MOD) {
            while  (CurrentToken().type == TokenType.MPY || 
                    CurrentToken().type == TokenType.DIV || 
                    CurrentToken().type == TokenType.MOD) {
                switch(CurrentToken().type)
                {
                    case MPY:
                        result = Multiply() * result;
                    break;
                    case DIV:                    
                        result = Divide() / result;                               
                    break;           
                    case MOD:
                        result = Mod() % result;
                    break;
                }
            }
        }
        else
            Error("! malformed term error !");
        return result;
    }
     //<factor>  <primary> ^ <factor> | <primary> //right associative, no fix
    public int Factor() {
        double result = Primary();
        if (CurrentToken().type == TokenType.POW) {
            result = Math.pow(result,Expo());            
        }
        else
            Error("! malformed factor error !");
        return (int) result;
    }
    //<primary>  <id> | <num> | ( <expr> )
    public int Primary() {
        int result = 0;
        if (CurrentToken().type == TokenType.ID) {
            Match(TokenType.ID);
        }
        else if (CurrentToken().type == TokenType.NUMBER) {            
            Match(TokenType.NUMBER);
            result = new Integer(CurrentToken().text).intValue();
        }
        else if (CurrentToken().type == TokenType.LEFT_PAREN) {
            Match(TokenType.LEFT_PAREN);
            result = Expr();
            Match(TokenType.RIGHT_PAREN);
        }
        else
            Error("! malformed primary error !");
        return result;
    }
    public void Error(String err) {
        System.out.println(err);
    }
    public void Print(List<Token> tokenList) {        
        for (Token token: tokenList)
        {
            if (token.type == TokenType.NUMBER) {
                System.out.println("PUSH\t\t" + token.text);
            }
            else if (token.type == TokenType.POP) {
                System.out.println("POP\t\t" + token.text);
            }
            else if (token.type == TokenType.STO) {
                System.out.println("STO\t\t" + token.text);
            }
            else if (token.type == TokenType.COPY) {
                System.out.println("COPY\t\t" + token.text);
            }
            else if (token.type == TokenType.LVALUE) {
                System.out.println("LVALUE\t\t" + token.text);
            }
            else if (token.type == TokenType.ID) {
                System.out.println("RVALUE\t\t" + token.text);
            }
            else if (token.type == TokenType.HALT) {
                System.out.println(token.type);
            }
            else if ( !(token.type == TokenType.RIGHT_PAREN ||
                    token.type == TokenType.LEFT_PAREN ||
                    token.type == TokenType.ASSIGNMENT ||
                    token.type == TokenType.SEMICOLON ||
                    token.type == TokenType.BEGIN ||
                    token.type == TokenType.BLANK ||
                    token.type == TokenType.TAB ||
                    token.type == TokenType.UNKNOWN)) {
                System.out.println(token.type);
            }
            /*
            else if (token.type == TokenType.UNKNOWN) {
                System.out.println("Token UNKN:\t~~~~");
            }
            else{System.out.println("White Space:\t" + token.type);}*/              
        }
    }

    public static void main(String args[]) throws IOException { 
        Parser myParser = new Parser();
        Lexer myLexer = new Lexer();
        
        //Scanner sc = new Scanner(System.in);
	//System.out.print("Enter file name to open: ");
 	//String inFile = sc.nextLine();
        FileInputStream fstream = new FileInputStream("ct2.txt");
        //FileReader fileReader = new FileReader("CompilerTest.txt");
        //DataInputStream inputFile = new DataInputStream(fstream);

        String entireFileText = "";
        boolean endOfFile = false;
        int i ;

        while((i =  fstream.read())!=-1){
            //try {
                char ch = (char)i;
                entireFileText = entireFileText + ch;
            //} catch (EOFException e){ endOfFile = true; }
            i++;
        }
        // Close the file
        fstream.close();
        
        System.out.println("Infix Expression: \n" + entireFileText + 
                "\n--------------------------------------------");
        //System.out.println("Postfix Expression: \n" + myLexer.Tokenize(entireFileText));
        myParser.tokenList = myLexer.Tokenize(entireFileText);
        myParser.Print(myParser.tokenList);
        int result = myParser.Program();// System.out.println( result );
    }
}