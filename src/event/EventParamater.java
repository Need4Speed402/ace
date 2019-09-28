package event;

import java.util.List;

import parser.Local;
import parser.Node;
import value.Value;

public class EventParamater implements Event{
	private final int level;
	private final Type type;
	
	private int height;
	
	public EventParamater(int level, Type type) {
		if (type == NONE) throw new IllegalArgumentException("Type cannot be null because by definition none isn't a paramater");
		
		this.level = level;
		this.type = type;
	}
	
	@Override
	public Value run(Local local) {
		return local.getParent(this.height).paramater;
	}
	
	@Override
	public void paramaterHeight(Node<Integer> pHeight, Node<Integer> mHeight) {
		if (type == MODIFIER) {
			this.height = mHeight.get(0) - mHeight.get(this.level + 1);
		}else if (type == PARAMATER) {
			this.height = pHeight.get(0) - pHeight.get(this.level + 1);
		}
	}
	
	@Override
	public void indexIdentifiers(EventFunction scope, List<EventIdentifier> idnt) {}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i <= this.level; i++) b.append('.');
		return b.toString();
	}
	
	public enum Type {
		NONE,
		MODIFIER,
		PARAMATER
	}
	
	public static Type NONE = Type.NONE;
	public static Type MODIFIER = Type.MODIFIER;
	public static Type PARAMATER = Type.PARAMATER;
}
