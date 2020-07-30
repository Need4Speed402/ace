package value.node;

import parser.Color;
import value.Value;
import value.ValueFunction;

public class NodeEnvironment implements Node{
	private final Node contents;
	
	protected NodeEnvironment(Node contents) {
		this.contents = contents;
	}
	
	public Value run(Value environment) {
		return new ValueFunction(arg ->
			this.contents.run(new ValueFunction(var ->
				arg.call(environment.call(var))
			, arg, environment))
		, environment);
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