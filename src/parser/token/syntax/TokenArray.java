package parser.token.syntax;

import parser.Color;
import parser.Stream;
import value.node.Node;

public class TokenArray extends TokenEnvironment{
	public TokenArray(Stream s) {
		super(readBlock(s, ']'));
	}
	
	@Override
	public String toString () {
		if (this.getTokens().length == 0) {
			return "[]";
		}else if (this.getTokens().length == 1) {
			return "[" + this.getTokens()[0].toString() + "]";
		}else {
			return "[\n" + Color.indent(TokenEnvironment.toString(this, '\n')) + "\n]";
		}
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id("Array"), super.createNode());
	}
}
