package value.resolver;

import java.util.HashMap;

import value.Value;
import value.Value.CallReturn;
import value.ValuePartial.Probe;
import value.intrinsic.Mutable.MutableProbe;

public class ResolverArgument extends Resolver{
	private final boolean locked;
	
	public ResolverArgument (Probe argument, Value value) {
		this.cache.put(argument, new CallReturn(value));
		this.locked = false;
	}

	public ResolverArgument (HashMap<Resolvable, CallReturn> cache) {
		super(cache);
		this.locked = true;
	}
	
	public ResolverArgument add (Probe p) {
		if (!this.cache.containsKey(p)) this.cache.put(p, new CallReturn(new Probe()));
		if (this.locked) return this;
		return new ResolverArgument(this.cache);
	}
	
	public MutableProbe setMutable (MutableProbe probe) {
		if (this.locked) return probe;

		CallReturn p = this.cache.get(probe);
		MutableProbe mutable;
		
		if (p == null) {
			mutable = new MutableProbe();
			this.cache.put(probe, new CallReturn(mutable));
		}else {
			mutable = (MutableProbe) p.value;
		}
		
		return mutable;
	}
}