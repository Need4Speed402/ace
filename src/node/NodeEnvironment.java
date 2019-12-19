package node;

import value.Value;
import value.ValueIdentifier;

public class NodeEnvironment implements Node{
	private final Node contents;
	
	public NodeEnvironment(Node contents) {
		this.contents = contents;
	}
	
	public Value run(Value environment) {
		return var -> this.contents.run(env -> var.call(new ValueIdentifier(((ValueIdentifier) env).name, environment.call(env))));
	}
	
	@Override
	public String toString() {
		return "{" + this.contents.toString() + "}";
	}
}