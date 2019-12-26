package parser.token;

import node.Node;

public class TokenOperatorTrailing implements Token, Modifier{
	private final Token token;
	private final String operator;
	
	public TokenOperatorTrailing(String operator, Token token) {
		this.operator = operator;
		this.token = token;
	}
	
	@Override
	public String toString() {
		return this.token.toString() + this.operator;
	}
	
	@Override
	public Node createNode() {
		return Node.call('`' + this.operator + '`', this.token.createNode());
	}
	
	@Override
	public Token bind(Token what) {
		return () -> Node.call('`' + this.operator + '`', this.token.createNode(), TokenBlock.createBlock(what.createNode()));
	}
	
	@Override
	public boolean isModifier() {
		return true;
	}
}
