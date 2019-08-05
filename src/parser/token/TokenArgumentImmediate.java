package parser.token;

import event.Event;
import event.EventCall;
import event.EventPFunc;

public class TokenArgumentImmediate extends Token{
	private int level;
	private Token immediate;
	
	public TokenArgumentImmediate (int level, Token immediate) {
		this.level = level;
		this.immediate = immediate;
	}
	
	@Override
	public Event createEvent() {
		return new EventCall(new EventPFunc(this.level), this.immediate.createEvent());
	}
	
	@Override
	public String toString () {
		StringBuilder b = new StringBuilder(this.level);
		
		for (int i = 0; i <= this.level; i++) b.append('.');
		
		b.append(this.immediate.toString());
		
		return b.toString();
	}
}
