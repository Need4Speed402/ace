package unsafe.lang;

import java.util.HashMap;

import value.Value;
import value.ValueIdentifier;

public class Scope implements Value{
	@Override
	public Value call(Value env) {
		return env.call(new ScopeEnv(x -> x));
	}

	public static class ScopeEnv implements Value{
		private HashMap<String, Value> memory;
		private final Value wrapper;
		private boolean closed = false;
		
		public ScopeEnv (Value wrapper) {
			this(wrapper, new HashMap<String, Value>());
			
			this.memory.put(ValueIdentifier.JOIN, a -> b -> {
				this.closed = true;
				
				if (Value.compare(a, ValueIdentifier.CONTINUE)) {
					return b.call(new ScopeEnv(this.wrapper, this.memory));
				}else{
					return Value.resolve(a);
				}
			});
			
			this.memory.put(ValueIdentifier.CONTINUE, v -> Value.NULL);
		}
		
		public ScopeEnv(Value wrapper, HashMap<String, Value> memory) {
			this.wrapper = wrapper;
			this.memory = memory;
		}

		@Override
		public Value call(Value var) {
			if (!(var instanceof ValueIdentifier)) return var;
			
			String name = ((ValueIdentifier) var).name;
			Value value = this.memory.getOrDefault(name, this.wrapper.call(var));
			
			return ctx -> {
				if (Value.compare(ctx, "`,")) {
					return body -> env -> arg -> body.call(ctx2 -> {
						if (Value.compare(ctx2, name)) {
							return g -> {
								if (Value.compare(g, "*`")) {
									return arg;
								}else {
									return arg.call(g);
								}
							};
						}else {
							return env.call(ctx2);
						}
					});
				}else if (Value.compare(ctx, ".`")) return local -> {
					if (!this.closed && Value.compare(local, "=")) {
						return set -> {
							this.memory.put(name, Value.resolve(set));
							return Value.NULL;
						};
					}
					
					return this.memory.getOrDefault(name, Value.NULL).call(local);
				};
				
				return value.call(ctx);
			};
		}
	}
}
