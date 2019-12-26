package parser.token;

import node.Node;

public class TokenOperatorLeading implements Token, Modifier{
	private final Token token;
	private final String operator;
	
	public TokenOperatorLeading(String operator, Token token) {
		this.operator = operator;
		this.token = token;
	}
	
	@Override
	public String toString() {
		return this.operator + this.token.toString();
	}
	
	@Override
	public Node createNode() {
		return Node.call(this.token.createNode(), Node.id('`' + this.operator + '`'));
	}
	
	@Override
	public boolean isModifier() {
		return this.token instanceof Modifier && ((Modifier) this.token).isModifier();
	}
}
