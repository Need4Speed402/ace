package event;

import java.util.List;

import parser.Local;
import parser.Node;
import value.Value;

public interface Event {
	public Value run (Local local);
	public void indexIdentifiers(EventFunction scope, List<EventIdentifier> idnt);
	public default void paramaterHeight (Node<Integer>[] nodes) {};
	
	public default void init () {}
	
	public static Event pipe (Object ... objects) {
		Event ret = null;
		
		for (int i = objects.length - 1; i >= 0; i--) {
			Object o = objects[i];
			Event e = null;
			
			if (o instanceof String) {
				e = new EventIdentifier((String) o);
			}else if (o instanceof Event) {
				e = (Event) o;
			}else if (o instanceof Event[]) {
				e = new EventIterator((Event[]) o);
			}else {
				throw new IllegalArgumentException(o.toString() + " must be either a string or an event");
			}
			
			if (ret == null) {
				ret = e;
			}else {
				ret = new EventCall(e, ret);
			}
		}
		
		return ret;
	}
}
