package parser.token.syntax;

import parser.token.Token;
import value.node.Node;

public class TokenImmediate implements Token{
	private final Token first, second;
	
	public TokenImmediate(Token first, Token second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public Node createNode() {
		return Node.call(this.first.createNode(), this.second.createNode());
	}

	@Override
	public String toString() {
		return "(" + this.first.toString() + ":" + this.second.toString() + ")";
	}
}
