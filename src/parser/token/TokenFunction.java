package parser.token;

import node.Node;
import node.NodeCall;
import node.NodeEnvironment;
import node.NodeIdentifier;
import node.NodeScope;

public class TokenFunction extends Token{
	private final Token param, body;
	
	public TokenFunction (Token param, Token body) {
		this.param = param;
		this.body = body;
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall(new NodeCall(new NodeIdentifier("Function"), this.param.createEvent()), new NodeEnvironment(new NodeScope(this.body.createEvent())));
	}
	
	@Override
	public String toString() {
		return this.param.toString() + ": " + this.body;
	}
}
