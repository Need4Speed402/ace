package event;

import java.util.List;

import parser.Local;
import parser.Node;
import value.Value;

public class EventIterator implements Event{
	private final Event[] elements;
	
	public EventIterator(Event[] elements) {
		this.elements = elements;
	}

	@Override
	public Value run(Local local) {
		Value v = Value.NULL;
		
		for (int i = this.elements.length - 1; i >= 0; i--) {
			Value vv = v;
			Value element = this.elements[i].run(local);
			
			v = p -> p.call(element).call(vv);
		}
		
		return v;
	}

	@Override
	public void indexIdentifiers(EventFunction scope, List<EventIdentifier> idnt) {
		for (Event e : this.elements) e.indexIdentifiers(scope, idnt);
	}
	
	@Override
	public void paramaterHeight(Node<Integer> pHeight, Node<Integer> mHeight) {
		for (Event e : this.elements) e.paramaterHeight(pHeight, mHeight);
	}
	
	@Override
	public void init() {
		for (Event e : this.elements) e.init();
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		
		for (int i = 0; i < this.elements.length; i++) {
			b.append(this.elements[i]);
			
			if (i + 1 < this.elements.length) {
				b.append("; ");
			}
		}
		
		b.append("]");
		return b.toString();
	}
}
