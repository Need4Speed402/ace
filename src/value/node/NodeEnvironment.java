package value.node;

import parser.Color;
import parser.ProbeSet.ProbeContainer;
import value.Value;
import value.ValueFunction;

public class NodeEnvironment implements Node{
	private final Node contents;
	private final ProbeContainer[] resolvers;
	
	protected NodeEnvironment(Node contents) {
		this.contents = contents;
		this.resolvers = null;
	}
	
	public NodeEnvironment (Node contents, ProbeContainer ... resolvers) {
		this.contents = contents;
		this.resolvers = resolvers;
	}
	
	public Value run(Value environment) {
		Node body = arg -> this.contents.run(new ValueFunction(var ->
			arg.call(environment.call(var))
		));
		
		if (this.resolvers == null) {
			return new ValueFunction(body);
		}else {
			return new ValueFunction(body, this.resolvers);
		}
	}
	
	@Override
	public String toString() {
		return "{\n" + Color.indent(this.contents.toString()) + "\n}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.resolvers != null) return false;
		
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