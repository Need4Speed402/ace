package parser.token;

import event.Event;
import event.EventPFunc;

public class TokenArgument extends Token{
	private int level;
	
	public TokenArgument (int level) {
		this.level = level;
	}
	
	@Override
	public Event createEvent() {
		return new EventPFunc(this.level);
	}
	
	@Override
	public String toString () {
		StringBuilder b = new StringBuilder(this.level);
		
		for (int i = 0; i <= this.level; i++) b.append('.');
		
		return b.toString();
	}
}
