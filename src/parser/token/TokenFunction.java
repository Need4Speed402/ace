package parser.token;

import node.Node;
import node.NodeCall;
import node.NodeIdentifier;

public class TokenFunction extends Token{
	private final Token param;
	
	public TokenFunction (Token param) {
		this.param = param;
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall(new NodeIdentifier("Parameter"), this.param.createEvent());
	}
	
	@Override
	public String toString() {
		return this.param.toString() + ",";
	}
}
