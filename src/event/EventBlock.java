package event;

import java.util.List;

import parser.Local;
import parser.Node;
import value.Value;
import value.ValueIdentifier;

public class EventBlock implements Event{
	public Event[] events;
	
	public EventBlock (Event[] events) {
		this.events = events;
	}
	
	@Override
	public Value run(Local local) {
		for (int i = 0; i < this.events.length; i++) {
			Value v = this.events[i].run(local);
			
			if (v instanceof ValueIdentifier) {
				v = ((ValueIdentifier) v).getReference();
			}
			
			if (v != Value.NULL) return v;
		}
		
		return Value.NULL;
	}
	
	@Override
	public void init() {
		for (int i = 0; i < this.events.length; i++) {
			this.events[i].init();
		}
	}

	@Override
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt) {
		for (int i = 0; i < this.events.length; i++) {
			this.events[i].indexIdentifiers(scope, idnt);
		}
	}
	
	@Override
	public void paramaterHeight(Node<Integer> pHeight, Node<Integer> mHeight) {
		for (int i = 0; i < this.events.length; i++) {
			this.events[i].paramaterHeight(pHeight, mHeight);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder ();
		
		for (int i = 0; i < this.events.length; i++) {
			b.append(this.events[i].toString());
			
			if (i + 1 < this.events.length) b.append('\n');
		}
		
		return b.toString();
	}
}
