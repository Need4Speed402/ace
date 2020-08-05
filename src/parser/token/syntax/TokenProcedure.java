package parser.token.syntax;

import parser.Color;
import parser.Stream;
import parser.token.Token;
import value.node.Node;

public class TokenProcedure extends TokenEnvironment {
	public TokenProcedure (Stream s) {
		super(readBlock(s, '}'));
	}
	
	public TokenProcedure (Token ... contents) {
		super(contents);
	}
	
	@Override
	public String toString () {
		if (this.getLength() <= 1) {
			return "{" + TokenEnvironment.toString(this, '\n') + "}";
		}else {
			return "{\n" + Color.indent(TokenEnvironment.toString(this, '\n')) + "\n}";
		}
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id("Procedure"), super.createNode());
	}
}
