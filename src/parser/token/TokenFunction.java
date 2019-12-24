package parser.token;

import node.Node;
import node.NodeCall;

public class TokenFunction extends Token{
	private final Token param;
	
	public TokenFunction (Token param) {
		this.param = param;
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall("Parameter", this.param.createEvent());
	}
	
	@Override
	public String toString() {
		return this.param.toString() + ",";
	}
}
