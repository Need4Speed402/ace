package parser.token;

import event.Event;
import event.EventCall;
import event.EventIdentifier;

public class TokenOperatorImmediate extends TokenCompound{
	private Token token;
	private final String operator;
	private final String op2;
	
	public TokenOperatorImmediate(String operator) {
		this.operator = operator;
		this.op2 = ("$" + operator).intern();
	}
	
	public TokenOperatorImmediate(String operator, Token token) {
		this(operator);
		this.token = token;
	}
	
	@Override
	public String toString() {
		return this.operator + this.token.toString();
	}
	
	@Override
	public Event createEvent() {
		return new EventCall(this.token.createEvent(), new EventIdentifier(op2));
	}
	
	@Override
	public void add(Token t) {
		this.token = t;
	}
}
