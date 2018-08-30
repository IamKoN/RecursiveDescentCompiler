package BooleanExprEvaluator;

import BooleanExprEvaluator.NonTerminal;

public class Or extends NonTerminal {
	public boolean interpret() {
		return left.interpret() || right.interpret();
	}

	public String toString() {
		return String.format("(%s | %s)", left, right);
	}
}
