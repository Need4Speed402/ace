package parser.token.syntax;

import parser.Stream;
import parser.token.Token;
import value.node.Node;

public class TokenBase extends TokenEnvironment{
	public TokenBase(Stream s) {
		this(s, '\0');
	}
	
	public TokenBase(Stream s, char terminator) {
		super(readBase(s, terminator));
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id("Package"), super.createNode());
	}
	
	private static Token[] readBase (Stream s, char terminator) {
		//ignore shebang
		if (s.isNext("#!")) {
			while (s.chr() != '\n');
		}
		
		return readBlock(s, terminator);
	}
}
