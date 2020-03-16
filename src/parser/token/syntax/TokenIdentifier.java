package parser.token.syntax;

import parser.Color;
import parser.Stream;
import parser.token.Modifier;
import parser.token.Token;
import value.Value;

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
	public Value createNode() {
		return Value.id(this.id);
	}
	
	@Override
	public boolean isModifier() {
		return Stream.uppercase.indexOf(this.id.charAt(0)) >= 0;
	}
}
