package BooleanExprEvaluator;

import BooleanExprEvaluator.Terminal;

public class False extends Terminal {
	public False() {
		super(false);
	}

	public boolean interpret() {
		return value;
	}
}
