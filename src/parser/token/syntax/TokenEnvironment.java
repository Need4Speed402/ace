package parser.token.syntax;

import parser.Color;
import parser.Stream;
import parser.token.Token;
import value.node.Node;

public class TokenEnvironment extends TokenProcedure {
	public TokenEnvironment (Stream s) {
		super(readBlock(s, '}'));
	}
	
	public TokenEnvironment (Token ... contents) {
		super(contents);
	}
	
	@Override
	public String toString () {
		if (this.getLength() <= 1) {
			return "{" + TokenProcedure.toString(this, '\n') + "}";
		}else {
			return "{\n" + Color.indent(TokenProcedure.toString(this, '\n')) + "\n}";
		}
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id("Environment"), super.createNode());
	}
}
