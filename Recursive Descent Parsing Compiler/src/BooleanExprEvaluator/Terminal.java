/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BooleanExprEvaluator;

public abstract class Terminal implements BooleanExpression{
	protected boolean value;

	public Terminal(boolean value) {
		this.value = value;
	}

	public String toString() {
		return String.format("%s", value);
	}
}
