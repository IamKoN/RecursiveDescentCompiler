/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BooleanExprEvaluator;

import BooleanExprEvaluator.*;

public class Test {
	public static void main(String[] args) {
		True t = new True();
		False f = new False();

		Or or = new Or();
		or.setLeft(t);
		or.setRight(f);

		Not not = new Not();
		not.setChild(f);
		And and = new And();
		and.setLeft(or);
		and.setRight(not);

		BooleanExpression root = and;

		System.out.println(root);
		System.out.println(root.interpret());
	}
}

