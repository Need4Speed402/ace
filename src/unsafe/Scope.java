package unsafe;

import java.util.HashMap;

import value.Value;
import value.ValueIdentifier;

public class Scope implements Value{
	@Override
	public Value call(Value env) {
		return env.call(new ScopeEnv());
	}

	private static class ScopeEnv implements Value{
		private HashMap<String, Value> memory;
		private boolean closed = false;
		
		public ScopeEnv () {
			this(new HashMap<String, Value>());
			
			memory.put("`,`", ident -> body -> arg -> body.call(ctx -> {
				if (Value.compare(ident, ctx)) {
					return g -> {
						if (Value.compare(g, "`*`")) {
							return arg;
						}else {
							return arg.call(g);
						}
					};
				}else {
					return Value.resolve(ctx);
				}
			}));
		}
		
		public ScopeEnv(HashMap<String, Value> memory) {
			this.memory = memory;
			
			this.memory.put("`", a -> b -> {
				this.closed = true;
				
				if (a != Value.NULL) {
					return Value.resolve(a);
				}else {
					return b.call(new ScopeEnv(this.memory));
				}
			});
		}

		@Override
		public Value call(Value var) {
			if (!(var instanceof ValueIdentifier)) return var;
			
			String name = ((ValueIdentifier) var).name;
			
			return ctx -> {
				if (Value.compare(ctx, "`.`")) return local -> {
					if (!this.closed && Value.compare(local, "=")) {
						return set -> {
							this.memory.put(name, set);
							return Value.NULL;
						};
					}
					
					return this.memory.getOrDefault(name, Value.NULL).call(local);
				};
				
				return this.memory.getOrDefault(name, Value.resolve(var)).call(ctx);
			};
		}
	}
}
