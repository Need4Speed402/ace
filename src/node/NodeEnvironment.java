package node;

import parser.token.TokenEnvironment;
import value.Value;
import value.ValueIdentifier;

public class NodeEnvironment implements Node{
	private final Node[] contents;
	
	public NodeEnvironment(Node ... contents) {
		this.contents = contents;
	}
	
	public Value run(Value environment) {
		if (this.contents.length == 0) return Value.NULL;
		
		return var -> {
			Value scope = env -> var.call(new ValueIdentifier(((ValueIdentifier) env).name, environment.call(env)));
			
			for (Node instruction : this.contents) {
				Value v = Value.resolve(instruction.run(scope));
				
				if (v != Value.NULL) {
					return v;
				}
			}
			
			return Value.NULL;
		};
	}
	
	@Override
	public String toString() {
		if (this.contents.length == 0) {
			return "{}";
		}else if (this.contents.length == 1) {
			return "{" + this.contents[0].toString() + "}";
		}else {
			StringBuilder b = new StringBuilder();
			
			for (int i = 0; i < this.contents.length; i++) {
				b.append(this.contents[i].toString());
				
				if (i + 1 < this.contents.length) {
					b.append('\n');
				}
			}
			
			return "{\n" + TokenEnvironment.indent(b.toString()) + "\n}";
		}
	}
}