package parser;

import java.util.Arrays;

import value.Value;
import value.ValueIdentifier;

public class Local{
	//private final Local[] parents;
	
	private final Local parent;
	public ValueIdentifier[] scope;
	public final Value paramater;
	
	public Local (Local parent, ValueIdentifier[] scope, Value paramater) {
		this.parent = parent;
		
		/*if (parent == null) {
			this.parents = new Local[] {this};
		}else {
			this.parents = new Local[parent.parents.length + 1];
			for (int i = parent.parents.length; i >= 1; i--) {
				this.parents[i] = parent.parents[i - 1];
			}
			
			this.parents[0] = this;
		}*/
		
		this.paramater = paramater;
		this.scope = scope;
	}
	
	@Override
	public String toString() {
		return "Local[scope: " + Arrays.toString(this.scope) + ", param: " + this.paramater + "]";
	}
	
	public Local getParent (int index){
		Local current = this;
		while (index-- > 0) current = current.parent;
		
		return current;
		
		//return this.parents[index];
	}
}
