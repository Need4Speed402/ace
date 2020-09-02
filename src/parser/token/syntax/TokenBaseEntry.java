package parser.token.syntax;

import parser.Stream;
import value.node.Node;

public class TokenBaseEntry extends TokenEnvironment{
	public TokenBaseEntry(Stream s) {
		this(s, '\0');
	}
	
	public TokenBaseEntry(Stream s, char terminator) {
		super(TokenBase.readBase(s, terminator));
	}

	@Override
	public Node createNode() {
		return Node.call(Node.id("EntryPackage"), super.createNode());
	}
}
