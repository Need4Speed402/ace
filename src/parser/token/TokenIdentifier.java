package parser.token;

import event.Event;
import event.EventIdentifier;

public class TokenIdentifier extends Token{
	public final String id;
	
	public TokenIdentifier (String key) {
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
	public Event createEvent() {
		return new EventIdentifier(this.id);
	}
}
