package parser.token.syntax;

import value.node.Node;
import parser.Color;
import parser.token.Modifier;
import parser.token.Token;

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
	public Node createNode() {
		return Node.call(this.token.createNode(), Node.id('`' + this.operator));
	}
	
	@Override
	public boolean isModifier() {
		return true;
	}
}
