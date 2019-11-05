package node;

import value.Value;

public class NodeCall implements Node{
	private Node function;
	private Node argument;
	
	public NodeCall (Node function, Node argument) {
		if (function == null || argument == null) throw new NullPointerException();
		
		this.function = function;
		this.argument = argument;
	}
	
	@Override
	public Value run(Value environment) {
		Value vf = this.function.run(environment);
		Value vp = this.argument.run(environment);
		
		return vf.call(vp);
	}
	
	@Override
	public String toString() {
		return "[" + this.function.toString() + " " + this.argument.toString() + "]";
	}
}
