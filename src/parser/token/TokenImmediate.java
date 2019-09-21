package parser.token;

import event.Event;
import event.EventCall;

public class TokenImmediate extends Token implements Modifier{
	private final Token first, second;
	
	public TokenImmediate(Token first, Token second) {
		this.first = first;
		this.second = second;
	}
	
	public Token getContent() {
		return first;
	}

	@Override
	public Event createEvent() {
		return new EventCall(this.first.createEvent(), this.second.createEvent());
	}

	@Override
	public String toString() {
		return this.first.toString() + this.second.toString();
	}
}
