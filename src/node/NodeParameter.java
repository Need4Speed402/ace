package node;

import java.util.List;

import parser.Local;
import parser.LinkedNode;
import value.Value;

public class NodeParameter implements Node{
	private final int level;
	private final Type type;
	
	private int height;
	
	public NodeParameter(int level, Type type) {
		this.level = level;
		this.type = type;
	}
	
	@Override
	public Value run(Local local, LinkedNode<Value> parameters) {
		return parameters.get(this.height);
	}
	
	@Override
	public void paramaterHeight(LinkedNode<Integer>[] nodes) {
		LinkedNode<Integer> node = nodes[this.type.ordinal()];
		this.height = node.get(0) - node.get(this.level + 1);
	}
	
	@Override
	public void indexIdentifiers(NodeScope scope, List<NodeIdentifier> idnt) {}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i <= this.height; i++) b.append('.');
		return b.toString();
	}
	
	public enum Type {
		NONE,
		MODIFIER,
		PARAMETER
	}
	
	public static Type NONE = Type.NONE;
	public static Type MODIFIER = Type.MODIFIER;
	public static Type PARAMETER = Type.PARAMETER;
	
	public static LinkedNode<Integer>[] createNodeList (){
		@SuppressWarnings("unchecked")
		LinkedNode<Integer>[] nodes = new LinkedNode[Type.values().length];
		
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new LinkedNode<Integer>(0);
		}
		
		return nodes;
	}
}
