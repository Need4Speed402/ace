package parser.token.syntax;

import value.node.Node;
import parser.Stream;

public class TokenBase extends TokenProcedure{
	public final boolean entry;
	
	public TokenBase(Stream s, boolean entry) {
		super(readBlock(s, '\0'));
		
		this.entry = entry;
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id(this.entry ? "EntryPackage" : "Package"), super.createNode());
	}
}
