package value.node;

import parser.token.syntax.TokenEnvironment;
import value.Value;
import value.ValueEffect;
import value.ValueFunction;

public class NodeEnvironment implements Node{
	private final Node contents;
	
	protected NodeEnvironment(Node contents) {
		this.contents = contents;
	}
	
	public Value run(Value environment) {
		return new ValueFunction(probe -> this.contents.run(var -> ValueEffect.wrap(var, probe.call(environment.call(ValueEffect.clear(var))))));
	}
	
	@Override
	public String toString() {
		return "{\n" + TokenEnvironment.indent(this.contents.toString()) + "\n}";
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