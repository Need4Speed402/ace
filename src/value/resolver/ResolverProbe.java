package value.resolver;

import parser.ProbeSet;
import value.resolver.Resolver;
import value.Value;
import value.ValuePartial.Probe;

public class ResolverProbe extends Resolver {
	private final Probe probe;
	private final Value value;
	
	public ResolverProbe (Probe probe, Value value) {
		this.probe = probe;
		this.value = value;
	}
	
	@Override
	public Value get(Probe p) {
		return this.probe == p ? this.value : p;
	}
	
	@Override
	public boolean has(ProbeSet set) {
		return set.has(this.probe);
	}
}