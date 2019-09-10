package event;

import java.util.List;

import parser.Local;
import value.Value;

public class EventNull implements Event{
	@Override
	public Value run(Local local) {
		return Value.NULL;
	}
	
	@Override
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt) {}
	
	@Override
	public String toString() {
		return "{}";
	}
}
