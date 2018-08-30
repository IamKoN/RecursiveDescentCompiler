/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RecursiveDescentCompiler;

class NewMain
{    
    public static String expression = "";   
    public static int currentCharPosition = 0;    
    public static char Look;
    
    public static void Init()
    {        
        GetChar();
    }    
    public static void GetChar()
    {
        if (currentCharPosition < expression.length())
            Look = expression.charAt(currentCharPosition);
            
        currentCharPosition++;
    }
    public static int GetNum()
    {
        int number = new Integer(Look+"").intValue();
        GetChar();
        
        return number; 
    }    
    public static void MatchAndEat(char chr)
    {
        if (Look==chr) GetChar(); else
        {
            System.out.println("Error: Unexpected character.");
            System.exit(0);
        }
    }    
    public static int Factor()
    {        
        int result = 0;       
        if (Look == '(')
        {
            MatchAndEat('(');
            result = ArithmeticExpression();
            MatchAndEat(')');
        } else result = GetNum();        
        return result;
    }
    public static int Multiply()
    {
        MatchAndEat('*');
        return Factor();
    }
    public static int Divide()
    {
        MatchAndEat('/');
        return Factor();
        
    }
    public static int Mod()
    {
        MatchAndEat('%');
        return Factor();
    }
    public static int Expo()
    {
        MatchAndEat('^');
        return Factor();
    }
    public static int Term()
    {        
        int result = Factor();                
        while ( (Look == '*') || (Look == '/') )
        {
            switch(Look)
            {
                case '*':
                    result = result * Multiply();
                break;
                case '/':
                    result = result / Divide();
                break;                               
            }
        }        
        return result;
    }
    public static int Add()
    {
        MatchAndEat('+');
        return Term();
    }
    public static int Subtract()
    {
        MatchAndEat('-');
        return Term();
    }
    public static int ArithmeticExpression()
    {        
        int result = Term();
        while ( (Look == '+') || (Look == '-') )
        {
            switch(Look)
            {
                case '+':
                    result = result + Add();
                break;
                case '-':
                    result = result - Subtract();
                break;                               
            }
        }        
        return result;
    }
    public static void main(String args[])
    {
        expression = "(9*3-1+8)*5-7";
        System.out.println("Expression: " + expression);
        Init();
        int result = ArithmeticExpression();
        System.out.println("Calculation Result: " + result);
    }    
}
