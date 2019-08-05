package event;

import java.util.List;

import parser.Local;
import parser.Node;
import value.Value;

public class EventMFunc implements Event{
	private int level;
	private int height;
	
	public EventMFunc(int level) {
		this.level = level;
	}
	
	@Override
	public Value run(Local local) {
		return local.getParent(this.height).paramater;
	}
	
	@Override
	public void paramaterHeight(Node<Integer> pHeight, Node<Integer> mHeight) {
		this.height = mHeight.get(0) - mHeight.get(this.level + 1);
	}
	
	@Override
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt) {}
}
