package node;

import value.Value;
import value.ValueIdentifier;

public class NodeEnvironment implements Node{
	private final Node contents;
	
	public NodeEnvironment (Node contents) {
		this.contents = contents;
	}
	
	@Override
	public Value run(Value environment) {
		return p -> {
			if (p instanceof ValueIdentifier) {
				p = ((ValueIdentifier) p).getReference();
			}
			
			Value pp = p;
			
			return this.contents.run(env -> {
				Value v = pp.call(new ValueIdentifier(((ValueIdentifier) env).id, environment));
				
				if (v == Value.NULL) {
					return environment.call(env);
				}else {
					return v;
				}
			});
		};
	}
	
	@Override
	public String toString() {
		return "{" + this.contents.toString() + "}";
	}
}