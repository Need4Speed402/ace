package value.resolver;

import value.Value;
import value.ValuePartial.Probe;

public class ResolverProbe extends Resolver {
	public final Probe probe;
	public final Value value;
	
	public ResolverProbe (Probe probe, Value value) {
		this.probe = probe;
		this.value = value;
	}
	
	@Override
	public Value get(Probe p) {
		return this.probe == p ? this.value : p;
	}
}