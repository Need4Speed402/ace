package value.resolver;

import java.util.HashMap;

import value.Value;
import value.ValuePartial.Probe;

public class ResolverArgument extends Resolver{
	private final Probe probe;
	private final Value value;
	private final Probe[] map;
	
	public ResolverArgument (Probe probe, Value value) {
		this.probe = probe;
		this.value = value;
		this.map = new Probe[0];
	}
	
	private ResolverArgument (Probe probe, Value value, Probe[] map) {
		this.probe = probe;
		this.value = value;
		this.map = map;
	}
	
	public ResolverArgument add (Probe p) {
		Probe[] np = new Probe[this.map.length + 1];
		System.arraycopy(this.map, 0, np, 0, this.map.length);
		np[this.map.length] = p;
		return new ResolverArgument(this.probe, this.value, np);
	}

	@Override
	public Value get(Probe p) {
		if (p == this.probe) {
			return value.resolve(new ResolverDedupProbe(this.map));
		}else {
			return p;
		}
	}
	
	private static class ResolverDedupProbe extends Resolver{
		private final HashMap<Probe, Probe> map;
		
		public ResolverDedupProbe (Probe[] map) {
			this.map = new HashMap<Probe, Probe>();
			
			for (int i = 0; i < map.length; i++) {
				this.map.put(map[i], new Probe());
			}
		}
		
		@Override
		public Value get(Probe p) {
			return this.map.getOrDefault(p, p);
		}
	}
}
