package value.node;

import value.Value;

public class NodeCall implements Node{
	private Node function;
	private Node argument;
	
	protected NodeCall (Node function, Node argument) {
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
		return "(" + this.function.toString() + " " + this.argument.toString() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NodeCall) {
			Node f = ((NodeCall) obj).function;
			Node a = ((NodeCall) obj).argument;
			
			return f.equals(this.function) && a.equals(this.argument);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return (this.function.hashCode() ^ this.argument.hashCode()) + 11;
	}
}
