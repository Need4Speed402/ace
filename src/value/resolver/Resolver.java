package value.resolver;

import java.util.HashMap;

import value.Value;
import value.ValuePartial.Probe;

public abstract class Resolver {
	private final HashMap<Value, Value> cache = new HashMap<>();

	public abstract Value get (Probe p);
	
	public Value cache (Value p) {
		Value v = this.cache.get(p);
		
		if (v == null) {
			v = p.resolve(this);
			this.cache.put(p, v);
		}

		return v;
	}
}
