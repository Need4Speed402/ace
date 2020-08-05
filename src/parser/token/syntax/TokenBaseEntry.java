package parser.token.syntax;

import parser.Stream;
import value.node.Node;

public class TokenBaseEntry extends TokenBase{
	public TokenBaseEntry(Stream s) {
		super(s, '\0');
	}
	
	public TokenBaseEntry(Stream s, char terminator) {
		super(s, terminator);
	}

	@Override
	public Node createNode() {
		return Node.call(Node.id("Package"), super.createNode());
	}
}
