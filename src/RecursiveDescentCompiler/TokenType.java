package RecursiveDescentCompiler;

public enum TokenType
{
    BEGIN, WAIT, HALT, EOF, PUSH, POP, STO, COPY,
    ADD, SUB, MPY, DIV, POW, MOD, ASSIGNMENT,
    RVALUE, LVALUE, OPERATOR, ID, KEYWORD, NUMBER, UNKNOWN,
    LEFT_PAREN, RIGHT_PAREN, SEMICOLON, BLANK, TAB, NULLSTRING, NEWLINE,
    //NOT, OR, AND, PRINT, PRINTLN
}