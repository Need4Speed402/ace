package node;

import java.util.List;

import parser.Global;
import parser.Local;
import value.Value;

public class NodeIdentifier implements Node{
	public final String name;
	public int location = -1;
	
	public NodeIdentifier(String name) {
		name = name.intern();
		
		Global.global.define(name);
		this.name = name;
	}
	
	@Override
	public Value run(Local local) {
		return local.scope[location];
	}
	
	@Override
	public void indexIdentifiers(NodeFunction scope, List<NodeIdentifier> idnt) {
		idnt.add(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
