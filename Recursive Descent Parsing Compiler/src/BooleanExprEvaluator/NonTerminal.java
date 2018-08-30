/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BooleanExprEvaluator;


public abstract class NonTerminal implements BooleanExpression {
	protected BooleanExpression left, right;

	public void setLeft(BooleanExpression left) {
		this.left = left;
	}

	public void setRight(BooleanExpression right) {
		this.right = right;
	}
}
