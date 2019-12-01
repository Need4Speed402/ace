package parser.resolver;

import java.util.HashMap;

import value.Value;
import value.ValueIdentifier;

public class ResolverScope implements Resolver{
	@Override
	public Value call(Value ctx) {
		if (Value.compare(ctx, "Scope")) {
			return env -> {
				Scope scope = new Scope();
				Value ret = env.call(scope);
				scope.close();
				return ret;
			};
		}
		return Resolver.NULL;
	}

	private static class Scope implements Value{
		private HashMap<String, Value> memory = new HashMap<String, Value>();
		private Value local;
		private boolean closed = false;
		
		public Scope () {
			this.local = parent -> var -> {
				if (var instanceof ValueIdentifier) {
					String name = ((ValueIdentifier) var).name;
					Value parentVar = parent.call(var);
					
					return ctx -> {
						if (!this.closed && Value.compare(ctx, "=")) {
							return set -> {
								if (set instanceof ValueIdentifier) {
									this.memory.put(name, a -> ((ValueIdentifier) set).value);
								}else {
									this.memory.put(name, a -> set);
								}
								
								return Value.NULL;
							};
						}else{
							return parentVar.call(ctx);
						}
					};
				}else {
					return var.call(".").call(this.local.call(parent));
				}
			};
			
			this.memory.put("local", this.local);
		}
		
		public void close () {
			this.closed = true;
		}

		@Override
		public Value call(Value var) {
			String name = ((ValueIdentifier) var).name;
			Value value = ((ValueIdentifier) var).value;
			
			return this.memory.getOrDefault(name, a -> a).call(value);
		}
	}
}
