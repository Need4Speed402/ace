package parser.token;

import event.Event;
import event.EventCall;
import event.EventMFunc;

public class TokenArgumentModifierImmediate extends Token{
	private int level;
	private Token immediate;
	
	public TokenArgumentModifierImmediate (int level, Token immediate) {
		this.level = level;
		this.immediate = immediate;
	}
	
	@Override
	public Event createEvent() {
		return new EventCall(new EventMFunc(this.level), this.immediate.createEvent());
	}
	
	@Override
	public String toString () {
		StringBuilder b = new StringBuilder(this.level);
		
		for (int i = 0; i <= this.level; i++) b.append(':');
		
		b.append(this.immediate.toString());
		
		return b.toString();
	}
}
