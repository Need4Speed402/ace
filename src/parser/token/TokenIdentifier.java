package parser.token;

import node.Node;
import node.NodeIdentifier;

public class TokenIdentifier extends Token{
	public final String id;
	
	public TokenIdentifier (String key) {
		if (key == null) throw new NullPointerException();
		
		this.id = key;
	}
	
	@Override
	public String toString() {
		return this.id;
	}
	
	public String getName() {
		return this.id;
	}
	
	@Override
	public Node createEvent() {
		return new NodeIdentifier(this.id);
	}
}
