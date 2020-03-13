package parser.token.syntax;

import parser.Color;

public class TokenOperator extends TokenIdentifier{
	public TokenOperator (String key) {
		super(key);
	}
	
	@Override
	public String toString() {
		return Color.red(super.toString());
	}
	
	@Override
	public boolean isModifier() {
		return false;
	}
}
