package parser.token;

import node.Node;
import node.NodeIdentifier;
import parser.Stream;

public class TokenIdentifier extends Token implements Modifier{
	public final String id;
	
	public TokenIdentifier (String key) {
		if (key == null) throw new NullPointerException();
		if (key.length() == 0) throw new IllegalArgumentException("identifier cannot be an empty string");
		
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
	
	@Override
	public boolean isModifier() {
		if (Stream.uppercase.indexOf(this.id.charAt(0)) >= 0) for (int i = 1; i < this.id.length(); i++) {
			if (TokenStatement.operators.indexOf(this.id.charAt(i)) >= 0) {
				continue;
			}
			
			if (Stream.uppercase.indexOf(this.id.charAt(i)) == -1) {
				return true;
			}
		}
		
		return false;
	}
}
