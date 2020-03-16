package parser.token.syntax;

import parser.Color;
import parser.token.Token;
import value.Value;

public class TokenOperatorLeading implements Token{
	private final Token token;
	private final String operator;
	
	public TokenOperatorLeading(String operator, Token token) {
		this.operator = operator;
		this.token = token;
	}
	
	@Override
	public String toString() {
		return Color.red(this.operator) + this.token.toString();
	}
	
	@Override
	public Value createNode() {
		return Value.call(this.token.createNode(), Value.id(this.operator + '`'));
	}
}
