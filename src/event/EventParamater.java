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
	public void paramaterHeight(Node<Integer>[] nodes) {
		Node<Integer> node = nodes[this.type.ordinal()];
		this.height = node.get(0) - node.get(this.level + 1);
	}
	
	@Override
	public void indexIdentifiers(EventFunction scope, List<EventIdentifier> idnt) {}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i <= this.height; i++) b.append('.');
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
	
	public static Node<Integer>[] createNodeList (){
		@SuppressWarnings("unchecked")
		Node<Integer>[] nodes = new Node[Type.values().length];
		
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new Node<Integer>(0);
		}
		
		return nodes;
	}
}
