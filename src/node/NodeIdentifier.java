package node;

import value.Value;

public class NodeIdentifier implements Node{
	public final String name;
	
	protected NodeIdentifier(String name) {
		this.name = name.intern();
	}
	
	@Override
	public Value run(Value environment) {
		return environment.call(new Value () {
			@Override
			public Value call(Value v) {
				return v;
			}
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public String toString() {
				return "Identifier(" + name + ")";
			}
		});
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
