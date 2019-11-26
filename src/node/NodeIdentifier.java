package node;

import value.Value;
import value.ValueIdentifier;

public class NodeIdentifier implements Node{
	public final String name;
	
	public NodeIdentifier(String name) {
		this.name = name.intern();
	}
	
	@Override
	public Value run(Value environment) {
		return new ValueIdentifier(this.name, environment.call(new ValueIdentifier(this.name, Value.NULL)));
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
