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
	
	public Value run(Value parentEnv) {
		if (this.contents.length != 0) {
			ScopeEnvironment scope = new ScopeEnvironment(parentEnv);
			
			for (int i = 0; i < this.contents.length; i++) {
				Value v = scope.run(this.contents[i]);
				
				if (v != Value.NULL) {
					if (v instanceof ValueIdentifier) {
						v = ((ValueIdentifier) v).getReference();
					}
					
					return v;
				};
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
	
	private class ScopeEnvironment {
		private HashMap<String, Value> memory = new HashMap<String, Value>();
		private final Value parentEnv;
		private int current = -1;
		
		public ScopeEnvironment (Value parentEnv) {
			this.parentEnv = parentEnv;
		}
		
		public Value run (Node node) {
			this.current++;
			Value res = node.run(this.env(this.current));
			this.current++;
			
			return res;
		}
		
		public Value env (int index) {
			return env -> {
				String name = ((ValueIdentifier) env).id;
				
				return p -> {
					if (index == this.current && Value.compare(p, "`.`")) {
						return p2 -> {
							if (Value.compare(p2, ":")) {
								return v -> {
									if (index == this.current) this.memory.put(name, v);
									return Value.NULL;
								};
							}else if (Value.compare(p2, "`*`")) {
								return this.memory.getOrDefault(name, Value.NULL);
							}else {
								return memory.getOrDefault(name, Value.NULL).call(p2);
							}
						};
					}
					
					Value ret = memory.get(name);
					if (ret == null) ret = this.parentEnv.call(env);
					
					return ret.call(p);
				};
			};
		}
		
	}
}