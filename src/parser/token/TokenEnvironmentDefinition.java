package parser.token;

import node.Node;
import node.NodeScope;

public class TokenEnvironmentDefinition extends Token{
	private final Token paramater;
	
	public TokenEnvironmentDefinition (Token paramater) {
		this.paramater = paramater;
	}
	
	public Token getParamater() {
		return paramater;
	}
	
	@Override
	public Node createEvent() {
		return new NodeScope();
	}
	
	@Override
	public String toString() {
		return "()";
	}
}
