package node;

import java.util.HashMap;

import parser.token.TokenEnvironment;
import value.Value;
import value.ValueIdentifier;

public class NodeScope implements Node{
	private final Node[] contents;
	
	public NodeScope(Node ... contents) {
		this.contents = contents;
	}
	
	public Value run(Value environment) {
		if (this.contents.length != 0) {
			HashMap<String, Value> memory = new HashMap<String, Value>();
			
			Value scope = env -> {
				String name = ((ValueIdentifier) env).id;
				
				return p -> {
					if (Value.compare(p, "`.`")) {
						return p2 -> {
							if (Value.compare(p2, "=")) {
								return p3 -> {
									memory.put(name, p3);
									return Value.NULL;
								};
							}else if (Value.compare(p2, "*")) {
								return memory.getOrDefault(name, Value.NULL);
							}else {
								return memory.getOrDefault(name, Value.NULL).call(p2);
							}
						};
					}
					
					Value ret = memory.get(name);
					if (ret == null) ret = environment.call(env);
					
					return ret.call(p);
				};
			};
			
			for (int i = 0; i < this.contents.length; i++) {
				Value v = this.contents[i].run(scope);
				
				while (v instanceof ValueIdentifier) v = ((ValueIdentifier) v).getReference();
				
				if (v != Value.NULL) return v;
			}
		}
		
		return Value.NULL;
	}

	@Override
	public String toString() {
		if (this.contents.length == 0) {
			return "()";
		}else if (this.contents.length == 1) {
			return "(" + this.contents[0].toString() + ")";
		}else {
			StringBuilder b = new StringBuilder();
			
			for (int i = 0; i < this.contents.length; i++) {
				b.append(this.contents[i].toString());
				
				if (i + 1 < this.contents.length) {
					b.append('\n');
				}
			}
			
			return "(\n" + TokenEnvironment.indent(b.toString()) + "\n)";
		}
	}
}