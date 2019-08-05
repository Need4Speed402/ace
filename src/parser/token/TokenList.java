package parser.token;

import event.Event;
import event.EventCall;
import event.EventIdentifier;
import event.EventList;

public class TokenList extends TokenCompound{
	@Override
	public String toString () {
		if (this.getTokens().length == 0) {
			return "[]";
		}else if (this.getTokens().length == 1) {
			return "[" + this.getTokens()[0].toString() + "]";
		}else {
			return "[\n" + TokenFunction.indent(TokenCompound.toString(this, '\n')) + "\n]";
		}
	}
	
	@Override
	public Event createEvent() {
		return new EventCall(new EventIdentifier("List"), new EventList(this.getEvents()));
	}
}
