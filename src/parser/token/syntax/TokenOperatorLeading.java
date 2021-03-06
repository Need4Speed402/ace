package parser.token.syntax;

import value.node.Node;
import parser.Color;
import parser.token.Token;

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
	public Node createNode() {
		return Node.call(this.token.createNode(), Node.id(this.operator + '`'));
	}
}
