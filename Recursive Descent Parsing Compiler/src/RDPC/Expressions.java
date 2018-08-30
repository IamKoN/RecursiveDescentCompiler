package RDPC;
/** 
 * <program>::= begin <stmt_list> end
 * <stmt>::=<id>::= <expr> | ε
 * <stmt_list>::=<stmt_list> ; <stmt> | <stmt>
 * <expr>::=<expr> + <term> | <expr> - <term> |  <term>
 * <term>::=<term> * <factor> | <term> div <factor> | <term> mod <factor> |  <factor>
 * <factor>::=<primary> ^ <factor> | <primary>
 * <primary>::=<id> | <num> | ( <expr> )
 */
public interface Expressions {
	public boolean interpret();
}
