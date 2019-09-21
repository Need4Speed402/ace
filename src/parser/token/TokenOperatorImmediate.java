package parser.token;

import event.Event;
import event.EventCall;
import event.EventIdentifier;

public class TokenOperatorImmediate extends Token implements Modifier{
	private final Token token;
	private final String operator;
	
	public TokenOperatorImmediate(String operator, Token token) {
		this.operator = operator;
		this.token = token;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public Token getContent() {
		return token;
	}
	
	@Override
	public String toString() {
		return this.operator + this.token.toString();
	}
	
	@Override
	public Event createEvent() {
		return new EventCall(this.token.createEvent(), new EventIdentifier('`' + this.operator + '`'));
	}
}
