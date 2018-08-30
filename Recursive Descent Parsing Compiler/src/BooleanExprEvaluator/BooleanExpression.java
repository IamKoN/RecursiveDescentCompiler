/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BooleanExprEvaluator;

/**
 * <expression>::=<term>{<or><term>}
 * <term>::=<factor>{<and><factor>}
 * <factor>::=<constant>|<not><factor>|(<expression>)
 * <constant>::= false|true
 * <or>::='|'
 * <and>::='&'
 * <not>::='!'
 */
public interface BooleanExpression {
	public boolean interpret();
}
