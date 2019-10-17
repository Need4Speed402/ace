package parser.token;

import node.Node;
import node.NodeBlock;
import node.NodeCall;
import node.NodeFunction;
import node.NodeIdentifier;
import node.NodeParameter;
import node.NodeScope;

public class TokenNamedFunction extends Token{
	private final Token argument, body;
	
	public TokenNamedFunction (Token argument, Token body) {
		this.argument = argument;
		this.body = body;
	}

	@Override
	public Node createEvent() {
		return new NodeFunction(new NodeScope(new NodeBlock(
				new NodeCall(new NodeCall(new NodeCall(argument.createEvent(), new NodeIdentifier("`>`")), new NodeIdentifier("=")), new NodeParameter(0, NodeParameter.NONE)),
				body.createEvent()
		)), NodeParameter.NONE);
	}
	
	@Override
	public String toString() {
		return this.argument.toString() + ": " + this.body.toString();
	}
}
