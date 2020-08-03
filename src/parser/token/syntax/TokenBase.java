package parser.token.syntax;

import parser.Stream;
import parser.token.Token;
import value.node.Node;

public class TokenBase extends TokenProcedure{
	public final boolean entry;
	
	public TokenBase(Stream s, boolean entry) {
		super(readBase(s));
		
		this.entry = entry;
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id(this.entry ? "EntryPackage" : "Package"), super.createNode());
	}
	
	private static Token[] readBase (Stream s) {
		//ignore shebang
		if (s.isNext("#!")) {
			while (s.chr() != '\n');
		}
		
		return readBlock(s, '\0');
	}
}
