package parser.token;

import node.Node;
import node.NodeCall;
import parser.Stream;

public class TokenBase extends TokenBlock{
	public TokenBase(Stream s) {
		super(readBlock(s, '\0'));
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall("Package", super.createEvent());
	}
}
