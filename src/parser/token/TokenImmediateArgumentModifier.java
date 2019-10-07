package parser.token;

import node.Node;
import node.NodeCall;
import node.NodeParameter;

public class TokenImmediateArgumentModifier extends Token implements Modifier{
	private final int level;
	private final Token content;
	
	public TokenImmediateArgumentModifier(int level, Token content) {
		this.level = level;
		this.content = content;
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall(new NodeParameter(level, NodeParameter.MODIFIER), content.createEvent());
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(this.level);
		
		for (int i = 0; i <= this.level; i++) b.append(':');
		
		return b.toString() + this.content.toString();
	}
	
	@Override
	public boolean isModifier() {
		return this.content instanceof Modifier && ((Modifier) this.content).isModifier();
	}
}
