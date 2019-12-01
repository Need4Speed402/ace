package parser.token;

import node.Node;
import node.NodeCall;
import node.NodeEnvironment;
import node.NodeIdentifier;

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
		return new NodeCall(new NodeIdentifier("Function"), new NodeEnvironment());
	}
	
	@Override
	public String toString() {
		return this.paramater.toString() + ",";
	}
}
