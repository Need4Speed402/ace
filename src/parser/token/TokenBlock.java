package parser.token;

import event.Event;
import event.EventBlock;

public class TokenBlock extends Token{
	Token[] tokens;
	
	public TokenBlock (Token[] tokens) {
		this.tokens = tokens;
	}
	
	public int getLength () {
		return this.tokens.length;
	}
	
	public Token[] getTokens() {
		return tokens;
	}
	
	@Override
	public String toString() {
		return toString(this, '\n');
	}
	
	protected static String toString(TokenBlock compound, char separator) {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < compound.tokens.length; i++) {
			b.append(compound.tokens[i].toString());
			
			if (i + 1 < compound.tokens.length) {
				b.append(separator);
			}
		}
		
		return b.toString();
	}
	
	public Event[] getEvents () {
		Event[] events = new Event[tokens.length];
		
		for (int i = 0; i < tokens.length; i++) {
			events[i] = tokens[i].createEvent();
		}
		
		return events;
	}
	
	@Override
	public Event createEvent() {
		return new EventBlock(this.getEvents());
	}
}
