package parser.token.syntax;

import parser.Color;
import parser.token.Modifier;
import parser.token.Token;
import value.Value;

public class TokenOperatorTrailing implements Token, Modifier{
	private final Token token;
	private final String operator;
	
	public TokenOperatorTrailing(String operator, Token token) {
		this.operator = operator;
		this.token = token;
	}
	
	@Override
	public String toString() {
		if (this.token instanceof TokenIdentifier) {
			return Color.purple(this.token.toString()) + Color.red(this.operator);
		}else{
			return this.token.toString() + Color.red(this.operator);
		}
	}
	
	@Override
	public Value createNode() {
		return Value.call(this.token.createNode(), Value.id('`' + this.operator));
	}
	
	@Override
	public boolean isModifier() {
		return true;
	}
}
