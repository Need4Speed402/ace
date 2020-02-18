package parser.token;

import value.node.Node;
import parser.Stream;

public class TokenBase extends TokenProcedure{
	public TokenBase(Stream s) {
		super(readBlock(s, '\0'));
	}
	
	@Override
	public Node createNode() {
		return Node.call("Package", super.createNode());
	}
}
