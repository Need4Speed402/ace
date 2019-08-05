package event;

import java.util.List;

import parser.Local;
import value.Value;

public class EventStatic implements Event{
	private Value value;
	
	public EventStatic (Value value) {
		this.value = value;
	}
	
	@Override
	public Value run(Local local) {
		return this.value;
	}
	
	@Override
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt) {}
}
