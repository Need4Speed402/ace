package value.resolver;

import value.Value;
import value.Value.CallReturn;
import value.ValuePartial.Probe;

public class ResolverMutable extends Resolver {
	public void put(Probe p, Value v) {
		this.cache.put(p, new CallReturn(v));
	}
	
	public Value set (Probe p, Value v) {
		if (this.cache.containsKey(p)){
			this.cache.put(p, new CallReturn(v));
			return null;
		}else {
			return v;
		}
	}
}
