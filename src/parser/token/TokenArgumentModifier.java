package parser.token;

import node.Node;
import node.NodeParameter;

public class TokenArgumentModifier extends Token{
	private int level;
	
	public TokenArgumentModifier (int level) {
		this.level = level;
	}
	
	@Override
	public Node createEvent() {
		return new NodeParameter(this.level, NodeParameter.MODIFIER);
	}
	
	@Override
	public String toString () {
		StringBuilder b = new StringBuilder(this.level);
		
		for (int i = 0; i <= this.level; i++) b.append(':');
		
		return b.toString();
	}
}
