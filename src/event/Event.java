package event;

import java.util.List;

import parser.Local;
import parser.Node;
import value.Value;

public interface Event {
	public Value run (Local local);
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt);
	public default void paramaterHeight (Node<Integer> pHeight, Node<Integer> mHeight) {};
	
	public default void init () {}
}
