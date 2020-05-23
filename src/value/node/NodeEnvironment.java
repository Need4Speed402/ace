package value.node;

import parser.Color;
import value.Value;
import value.ValueDefer;

public class NodeEnvironment implements Node{
	private final Node contents;
	
	protected NodeEnvironment(Node contents) {
		this.contents = contents;
	}
	
	public Value run(Value environment) {
		return ValueDefer.accept(arg ->
			this.contents.run(ValueDefer.accept(var ->
				arg.call(environment.call(var))
			))
		);
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