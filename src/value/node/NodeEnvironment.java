package value.node;

import parser.Color;
import value.Value;
import value.ValueFunction;

public class NodeEnvironment implements Node{
	private final ValueFunction contents;
	
	public NodeEnvironment(Node contents) {
		this.contents = new ValueFunction(contents);
	}
	
	public Value run(Value environment) {
		return new ValueFunction(handler -> this.contents.call(new ValueFunction(var -> handler.call(environment.call(var)))));
	}
	
	@Override
	public String toString() {
		return "{\n" + Color.indent(this.contents.toString()) + "\n}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NodeEnvironment) {
			return ((NodeEnvironment) obj).contents.equals(obj);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.contents.hashCode() + 7;
	}
}