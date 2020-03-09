package parser.token;

import value.node.Node;
import parser.Stream;

public class TokenArray extends TokenProcedure{
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
			return "[\n" + TokenEnvironment.indent(TokenProcedure.toString(this, '\n')) + "\n]";
		}
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id("Array"), super.createNode());
	}
}
