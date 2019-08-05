package parser.token;

import event.Event;
import event.EventMFunc;

public class TokenArgumentModifier extends Token{
	private int level;
	
	public TokenArgumentModifier (int level) {
		this.level = level;
	}
	
	@Override
	public Event createEvent() {
		return new EventMFunc(this.level);
	}
	
	@Override
	public String toString () {
		StringBuilder b = new StringBuilder(this.level);
		
		for (int i = 0; i <= this.level; i++) b.append(':');
		
		return b.toString();
	}
}
