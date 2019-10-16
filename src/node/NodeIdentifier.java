package node;

import java.util.List;

import parser.LinkedNode;
import parser.Local;
import value.Value;

public class NodeIdentifier implements Node{
	public final String name;
	public int location = -1;
	
	public NodeIdentifier(String name) {
		this.name = name.intern();
	}
	
	@Override
	public void init(Local global) {}
	
	@Override
	public Value run(Local local, LinkedNode<Value> parameters) {
		return local.scope[this.location];
	}
	
	@Override
	public void indexIdentifiers(NodeScope scope, List<NodeIdentifier> idnt) {
		idnt.add(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
