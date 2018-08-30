package RecursiveDescentCompiler;

class Token
{
    public final String text;
    public final TokenType type;

    public Token(String text, TokenType type)
    {
        this.text = text;
        this.type = type;
    }
    public String toString() 
    {
        return "Text: " + text + "\tType: " + type; 
    }
}
