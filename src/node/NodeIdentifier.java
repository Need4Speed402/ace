package node;

import value.Value;
import value.ValueIdentifier;

public class NodeIdentifier implements Node{
	public final String name;
	
	protected NodeIdentifier(String name) {
		this.name = name.intern();
	}
	
	@Override
	public Value run(Value environment) {
		return new ValueIdentifier(this.name, environment.call(new ValueIdentifier(this.name, v -> v)));
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NodeIdentifier) {
			return ((NodeIdentifier) obj).name == name;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
