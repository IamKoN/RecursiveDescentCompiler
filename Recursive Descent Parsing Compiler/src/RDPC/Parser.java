/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RDPC;

import BooleanExprEvaluator.*;

public class Parser {
	private Lexer lexer;
	private int symbol;
	private BooleanExpression root;

	private final True t = new True();
	private final False f = new False();

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	public BooleanExpression build() {
		expression();
		return root;
	}

	private void expression() {
		term();
		while (symbol == Lexer.OR) {
			Or or = new Or();
			or.setLeft(root);
			term();
			or.setRight(root);
			root = or;
		}
	}

	private void term() {
		factor();
		while (symbol == Lexer.AND) {
			And and = new And();
			and.setLeft(root);
			factor();
			and.setRight(root);
			root = and;
		}
	}

	private void factor() {
		symbol = lexer.nextSymbol();
		if (symbol == Lexer.TRUE) {
			root = t;
			symbol = lexer.nextSymbol();
		} else if (symbol == Lexer.FALSE) {
			root = f;
			symbol = lexer.nextSymbol();
		} else if (symbol == Lexer.NOT) {
			Not not = new Not();
			factor();
			not.setChild(root);
			root = not;
		} else if (symbol == Lexer.LEFT) {
			expression();
			symbol = lexer.nextSymbol(); // we don't care about ')'
		} else {
			throw new RuntimeException("Expression Malformed");
		}
	}
        private void program(){
            String b = "begin";
            String e = "end";
            while( ){
                b;
                stmtList();
                e;
                
            }
        
        
        }
        private void stmt(){}
        private void stmtList(){
            stmtList();
            stmt() OR stmt();
            while (symbol == Lexer.OR) {
			Or or = new Or();
			or.setLeft(root);
			stmt();
			or.setRight(root);
			root = or;
		}
        }
        private void expr(){}
        private void term(){}
        private void factor(){}
        private void primary(){}
         * <program>::= begin <stmt_list> end
        * <stmt>::=<id>::= <expr> | ε
        * <stmt_list>::=<stmt_list> ; <stmt> | <stmt>
        * <expr>::=<expr> + <term> | <expr> - <term> |  <term>
        * <term>::=<term> * <factor> | <term> div <factor> | <term> mod <factor> |  <factor>
        * <factor>::=<primary> ^ <factor> | <primary>
        * <primary>::=<id> | <num> | ( <expr> )
}

