package unsafe;

import java.util.HashMap;

import value.Value;
import value.ValueIdentifier;

public class Scope implements Value{
	@Override
	public Value call(Value env) {
		ScopeEnv scope = new ScopeEnv();
		Value ret = env.call(scope);
		scope.close();
		return ret;
	}

	private static class ScopeEnv implements Value{
		private HashMap<String, Value> memory = new HashMap<String, Value>();
		private Value local;
		private boolean closed = false;
		
		public ScopeEnv () {
			this.local = parent -> var -> {
				if (var instanceof ValueIdentifier) {
					String name = ((ValueIdentifier) var).name;
					Value parentVar = parent.call(var);
					
					return ctx -> {
						if (!this.closed && Value.compare(ctx, "=")) {
							return set -> {
								Value put = Value.resolve(set);
								this.memory.put(name, a -> put);
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
