package event;

import parser.Local;
import parser.Node;
import value.Value;

public class EventFunction extends EventScope{
	public EventFunction (Event contents) {
		super(contents);
	}
	
	@Override
	public Value run(Local local) {
		return new Value(p -> super.run(local, p));
	}

	@Override
	public void paramaterHeight(Node<Integer> pHeight, Node<Integer> mHeight) {
		this.contents.paramaterHeight(pHeight.replace(pHeight.get(0) + 1).add(pHeight.get(0) + 1), mHeight.replace(mHeight.get(0) + 1));
	}
}