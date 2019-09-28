package parser.token;

import event.Event;
import event.EventFunction;
import event.EventParamater;
import parser.Stream;

public class TokenFunction extends TokenBlock {
	public TokenFunction (Stream s) {
		super(TokenBase.readBlock(s, '}'));
	}
	
	public static String indent (String s) {
		StringBuilder ss = new StringBuilder();
		ss.append('\t');
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			
			if (c == '\n') {
				ss.append('\n').append('\t');
			}else {
				ss.append(c);
			}
		}
		
		return ss.toString();
	}
	
	@Override
	public String toString () {
		if (this.getLength() <= 1) {
			return "{" + TokenBlock.toString(this, '\n') + "}";
		}else {
			return "{\n" + indent(TokenBlock.toString(this, '\n')) + "\n}";
		}
	}
	
	public Event createModifierEvent () {
		return this.createEvent(EventParamater.MODIFIER);
	}
	
	public Event createHiddenEvent () {
		return this.createEvent(EventParamater.NONE);
	}
	
	@Override
	public Event createEvent() {
		return this.createEvent(EventParamater.PARAMATER);
	}
	
	private Event createEvent(EventParamater.Type type) {
		return new EventFunction(super.createEvent(), type);
	}
}
