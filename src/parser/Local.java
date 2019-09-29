package parser;

import java.util.Arrays;

import value.ValueIdentifier;

public class Local{
	private final Local parent;
	public ValueIdentifier[] scope;
	
	public Local (Local parent, ValueIdentifier[] scope) {
		this.parent = parent;
		
		this.scope = scope;
	}
	
	@Override
	public String toString() {
		return "Local[" + Arrays.toString(this.scope) + "]";
	}
	
	public Local getParent (int index){
		Local current = this;
		while (index-- > 0) current = current.parent;
		
		return current;
	}
}
