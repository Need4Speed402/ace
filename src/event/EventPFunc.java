package event;

import java.util.List;

import parser.Local;
import parser.Node;
import value.Value;

public class EventPFunc implements Event{
	private int level;
	private int height;
	
	public EventPFunc(int level) {
		this.level = level;
	}
	
	@Override
	public Value run(Local local) {
		return local.getParent(this.height).paramater;
	}
	
	@Override
	public void paramaterHeight(Node<Integer> pHeight, Node<Integer> mHeight) {
		this.height = pHeight.get(0) - pHeight.get(this.level + 1);
	}
	
	@Override
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt) {}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i <= this.level; i++) b.append('.');
		return b.toString();
	}
}
