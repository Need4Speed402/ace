package parser.token;

import node.Node;
import node.NodeCall;

public class TokenImmediate extends Token implements Modifier{
	private final Token first, second;
	
	public TokenImmediate(Token first, Token second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall(this.first.createEvent(), this.second.createEvent());
	}

	@Override
	public String toString() {
		return "(" + this.first.toString() + ":" + this.second.toString() + ")";
	}
	
	@Override
	public boolean isModifier() {
		return this.first instanceof Modifier && ((Modifier) this.first).isModifier();
	}
}
