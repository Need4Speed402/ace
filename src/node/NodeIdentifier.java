package node;

import value.Value;
import value.ValueIdentifier;

public class NodeIdentifier implements Node{
	public final String name;
	public int location = -1;
	
	public NodeIdentifier(String name) {
		this.name = name.intern();
	}
	
	@Override
	public Value run(Value environment) {
		return new ValueIdentifier(this.name, environment);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
