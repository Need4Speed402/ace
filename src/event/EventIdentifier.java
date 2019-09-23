package event;

import java.util.List;

import parser.Global;
import parser.Local;
import value.Value;

public class EventIdentifier implements Event{
	public final String name;
	public int location = -1;
	
	public EventIdentifier(String name) {
		name = name.intern();
		
		Global.global.define(name);
		this.name = name;
	}
	
	@Override
	public Value run(Local local) {
		return local.scope[location];
	}
	
	@Override
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt) {
		idnt.add(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
