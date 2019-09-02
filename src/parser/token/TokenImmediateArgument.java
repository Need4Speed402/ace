package parser.token;

import event.Event;
import event.EventCall;
import event.EventPFunc;

public class TokenImmediateArgument extends Token{
	private final int level;
	private final Token content;
	
	public TokenImmediateArgument(int level, Token content) {
		this.level = level;
		this.content = content;
	}
	
	@Override
	public Event createEvent() {
		return new EventCall(new EventPFunc(level), content.createEvent());
	}
	
	public Token getContent() {
		return content;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(this.level);
		
		for (int i = 0; i <= this.level; i++) b.append('.');
		
		return b.toString() + this.content.toString();
	}
}
