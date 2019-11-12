package parser.token;

import node.Node;
import node.NodeCall;
import node.NodeIdentifier;
import node.NodeScope;

public class TokenFunctionDefinition extends Token{
	private final Token paramater;
	
	public TokenFunctionDefinition (Token paramater) {
		this.paramater = paramater;
	}
	
	public Token getParamater() {
		return paramater;
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall(new NodeIdentifier("Function"), new NodeScope());
	}
	
	@Override
	public String toString() {
		return this.paramater.toString() + ",";
	}
}
