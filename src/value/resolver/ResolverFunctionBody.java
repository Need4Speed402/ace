package value.resolver;

import java.util.HashMap;

import parser.ProbeSet;
import value.ValuePartial.Probe;

public class ResolverFunctionBody extends Resolver{
	private final HashMap<Probe, Probe> defs;
	private final boolean locked;
	
	public ResolverFunctionBody() {
		this(new HashMap<>(), false);
	}
	
	private ResolverFunctionBody(HashMap<Probe, Probe> defs, boolean locked) {
		this.locked = locked;
		this.defs = defs;
	}
	
	public ResolverFunctionBody lock () {
		if (this.locked) return this;
		if (this.defs.size() == 0) return null;
		
		return new ResolverFunctionBody(this.defs, true);
	}
	
	public Probe set (Probe probe) {
		if (this.locked) return probe;
		
		Probe p = this.defs.get(probe);
		
		if (p == null) {
			p = new Probe();
			this.defs.put(probe, p);
		}
		
		return p;
	}
	
	@Override
	public Probe get(Probe p) {
		return this.defs.getOrDefault(p, p);
	}

	@Override
	public boolean has(ProbeSet set) {
		for (Probe p : this.defs.keySet()) {
			if (set.has(p)) return true;
		}
		
		return false;
	}
}
