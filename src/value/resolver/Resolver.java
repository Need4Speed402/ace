package value.resolver;

import java.util.HashMap;

import runtime.Effect;
import value.Value;
import value.Value.CallReturn;

public abstract class Resolver {
	public final HashMap<Resolvable, CallReturn> cache;
	
	public Resolver () {
		this.cache = new HashMap<>();	
	}
	
	public Resolver (HashMap<Resolvable, CallReturn> cache) {
		this.cache = cache;
	}

	private CallReturn cache (Resolvable obj) {
		CallReturn out = this.cache.get(obj);
		
		if (out == null) {
			out = obj.resolve(this);
			this.cache.put(obj, out);
		}

		return out;
	}
	
	public Value resolveValue (Resolvable obj) {
		return this.cache(obj).value;
	}
	
	public Effect resolveEffect (Resolvable obj) {
		return this.cache(obj).effect;
	}
	
	public static interface Resolvable {
		public CallReturn resolve (Resolver resolver);
	}
}
