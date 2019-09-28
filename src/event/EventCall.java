package event;

import java.util.List;

import parser.Local;
import parser.Node;
import value.Value;

public class EventCall implements Event{
	private Event function;
	private Event argument;
	
	public EventCall (Event function, Event argument) {
		if (function == null || argument == null) throw new NullPointerException();
		
		this.function = function;
		this.argument = argument;
	}
	
	@Override
	public Value run(Local local) {
		return function.run(local).call(argument.run(local));
	}
	
	@Override
	public void init() {
		this.function.init();
		this.argument.init();
	}
	
	@Override
	public void indexIdentifiers(EventFunction scope, List<EventIdentifier> idnt) {
		this.function.indexIdentifiers(scope, idnt);
		this.argument.indexIdentifiers(scope, idnt);
	}
	
	@Override
	public void paramaterHeight(Node<Integer>[] nodes) {
		this.function.paramaterHeight(nodes);
		this.argument.paramaterHeight(nodes);
	}
	
	@Override
	public String toString() {
		return "(" + this.function.toString() + " " + this.argument.toString() + ")";
	}
}
