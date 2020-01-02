package parser.token;

import node.Node;
import parser.Color;

public class TokenOperatorTrailing implements Token, Modifier{
	private final Token token;
	private final String operator;
	
	public TokenOperatorTrailing(String operator, Token token) {
		this.operator = operator;
		this.token = token;
	}
	
	@Override
	public String toString() {
		return this.token.toString() + Color.red(this.operator);
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
