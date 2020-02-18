package value.node;

import parser.token.TokenEnvironment;
import value.Value;

public class NodeEnvironment implements Node{
	private final Node contents;
	
	protected NodeEnvironment(Node contents) {
		this.contents = contents;
	}
	
	public Value run(Value environment) {
		return var -> this.contents.run(env -> var.call(environment.call(env)));
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