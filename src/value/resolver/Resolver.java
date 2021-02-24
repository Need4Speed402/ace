package value.resolver;

import java.util.HashMap;

import value.Value;
import value.Value.CallReturn;
import value.ValuePartial.Probe;

public abstract class Resolver {
	private final HashMap<Pair, CallReturn> cache = new HashMap<>();

	public abstract Value get (Probe p);
	
	public CallReturn call (Value function, Value argument) {
		Pair p = new Pair(function, argument);
		CallReturn v = this.cache.get(p);
		
		if (v == null) {
			v = function.resolve(this).call(argument.resolve(this));
			this.cache.put(p, v);
		}

		return v;
	}
	
	private static class Pair {
		public final Value function, argument;
		
		public Pair (Value function, Value argument) {
			this.function = function;
			this.argument = argument;
		}
		
		@Override
		public int hashCode() {
			return this.function.hashCode() + this.argument.hashCode() + 7;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Pair) {
				return ((Pair) obj).function.equals(this.function) && ((Pair) obj).argument.equals(this.argument);
			}

			return false;
		}
	}
}
