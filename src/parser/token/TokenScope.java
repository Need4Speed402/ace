package parser.token;

import event.Event;
import event.EventFunction;
import parser.Stream;

public class TokenScope extends TokenBlock{
	public TokenScope (Stream s) {
		super(TokenBase.readBlock(s, ')'));
	}
	
	@Override
	public String toString (){
		if (this.getTokens().length == 0) {
			return "()";
		}else if (this.getTokens().length == 1) {
			return "(" + this.getTokens()[0].toString() + ")";
		}else {
			return "(\n" + TokenFunction.indent(TokenBlock.toString(this, '\n')) + "\n)";
		}
	}
	
	@Override
	public Event createEvent() {
		return EventFunction.createScope(super.createEvent());
	}
}
