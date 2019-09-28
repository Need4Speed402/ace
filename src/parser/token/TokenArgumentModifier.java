package parser.token;

import event.Event;
import event.EventParamater;

public class TokenArgumentModifier extends Token{
	private int level;
	
	public TokenArgumentModifier (int level) {
		this.level = level;
	}
	
	@Override
	public Event createEvent() {
		return new EventParamater(this.level, EventParamater.MODIFIER);
	}
	
	@Override
	public String toString () {
		StringBuilder b = new StringBuilder(this.level);
		
		for (int i = 0; i <= this.level; i++) b.append(':');
		
		return b.toString();
	}
}
