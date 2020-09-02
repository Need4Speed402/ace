package value.resolver;

import java.util.HashMap;

import parser.ProbeSet;
import value.Value;
import value.ValuePartial.Probe;

public class ResolverMutable extends Resolver {
	private final HashMap<Probe, Value> memory = new HashMap<>();
	
	public void put(Probe p, Value v) {
		this.memory.put(p, v);
	}
	
	public Value set (Probe p, Value v) {
		if (this.memory.containsKey(p)){
			this.memory.put(p, v);
			return null;
		}else {
			return v;
		}
	}
	
	@Override
	public Value get(Probe p) {
		return this.memory.getOrDefault(p, p);
	}
	
	public HashMap<Probe, Value> getMap() {
		return this.memory;
	}

	@Override
	public boolean has(ProbeSet set) {
		for (Probe p : this.memory.keySet()) {
			if (set.has(p)) return true;
		}
		
		return false;
	}
	
	public boolean has(Probe p) {
		return this.memory.containsKey(p);
	}
	
	@Override
	public String toString() {
		return "MutableResolver(items: " + memory.size() + ")";
	}
}
