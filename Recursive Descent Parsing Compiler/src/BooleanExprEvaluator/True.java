package BooleanExprEvaluator;

import BooleanExprEvaluator.Terminal;

public class True extends Terminal {
	public True() {
		super(true);
	}

	public boolean interpret() {
		return value;
	}
}
