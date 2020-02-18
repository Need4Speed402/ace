package parser.token;

import value.node.Node;
import parser.Color;
import parser.Stream;

public class TokenIdentifier implements Token, Modifier{
	private final String id;
	
	public TokenIdentifier (String key) {
		if (key == null) throw new NullPointerException();
		if (key.length() == 0) throw new IllegalArgumentException("identifier cannot be an empty string");
		
		this.id = key;
	}
	
	@Override
	public String toString() {
		if (this.isModifier()) {
			return Color.cyan(this.id);
		}else {
			return this.id;
		}
	}
	
	public String getName() {
		return this.id;
	}
	
	@Override
	public Node createNode() {
		return Node.id(this.id);
	}
	
	@Override
	public boolean isModifier() {
		return Stream.uppercase.indexOf(this.id.charAt(0)) >= 0;
	}
}
