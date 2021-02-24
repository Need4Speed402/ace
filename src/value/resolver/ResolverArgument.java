package value.resolver;

import java.util.HashMap;

import value.Value;
import value.ValuePartial.Probe;

public class ResolverArgument extends Resolver{
	private final HashMap<Probe, Value> map = new HashMap<>();
	
	public ResolverArgument (Probe argument, Value value) {
		this.map.put(argument, value);
	}
	
	public void add (Probe p) {
		if (!this.map.containsKey(p)) this.map.put(p, new Probe());
	}

	@Override
	public Value get(Probe p) {
		return this.map.getOrDefault(p, p);
	}
}
