package parser;

import value.ValuePartial.Probe;

// simple hash set implementation that uses linear probing
// designed to be memory efficient.
public class ProbeSet{
	private int[] set = new int[8];
	private int size, count;
	
	public ProbeSet () {}
	
	public ProbeSet (ProbeSet.ProbeContainer ... probes) {
		this ();
		for (ProbeSet.ProbeContainer p : probes) p.getResolves(this);
	}
	
	private ProbeSet(ProbeSet set) {
		this.set = set.set.clone();
		this.size = set.size;
	}
	
	private ProbeSet(ProbeSet set, int remove) {
		this.size = set.size;
		this.set = new int[set.set.length];
		this.count = set.count();
		
		for (int i = 0; i < set.set.length; i += 2) {
			if (set.set[i] == remove) {
				this.size--;
				this.count -= set.set[i + 1];
				continue;
			}
			
			if (set.set[i] != 0) set(this.set, set.set[i], set.set[i + 1]);
		}
	}
	
	public int count(Probe probe) {
		int hash = (probe.id % (this.set.length >> 1)) << 1;
		
		while (this.set[hash] != 0) {
			if (this.set[hash] == probe.id) return this.set[hash + 1];
			
			hash += 2;
			if (hash >= this.set.length) hash = 0;
		}
		
		return 0;
	}
	
	public int count () {
		return this.count;
	}
	
	public boolean has (Probe probe) {
		return this.count(probe) > 0;
	}
	
	public void ensureAvailable (int size) {
		int wants = this.set.length;
		while ((this.size + size) * 4 > wants) {
			wants *= 2;
		}
		
		if (wants != this.set.length) {
			int[] set = this.set;
			this.set = new int[wants];
			
			for (int i = 0; i < set.length; i += 2) {
				if (set[i] > 0) set(this.set, set[i], set[i + 1]);
			}
		}
	}
	
	public void set (ProbeSet set) {
		this.ensureAvailable(set.size);
		
		this.count += set.count();
		
		for (int i = 0; i < set.set.length; i += 2) {
			if (set.set[i] != 0) {
				if (set(this.set, set.set[i], set.set[i + 1])) this.size++;
			}
		}
	}
	
	public void set (Probe ... probes) {
		this.ensureAvailable(probes.length);
		
		this.count += probes.length;
		
		for (Probe p : probes) {
			if (set(this.set, p.id, 1)) this.size++;
		}
	}
	
	public static boolean set (int[] set, int id, int inc) {
		int hash = (id % (set.length >> 1)) << 1;
		
		while (set[hash] != 0) {
			if (set[hash] == id) {
				set[hash + 1] += inc;
				return false;
			}
			
			hash += 2;
			if (hash >= set.length) hash = 0;
		}
		
		set[hash] = id;
		set[hash + 1] = inc;
		return true;
	}
	
	public int size () {
		return this.size;
	}
	
	@Override
	public String toString() {
		if (this.size == 0) return "[]";
		
		StringBuilder b = new StringBuilder();
		b.append('[');
		
		int l = 0;
		for (int i = 0; i < this.set.length; i += 2) {
			if (this.set[i] != 0) {
				b.append("Probe(").append(this.set[i]).append(")");
				l++;
				if (l == this.size) break;
				b.append(", ");
			}
		}
		
		b.append("]");
		return b.toString();
	}
	
	public ProbeSet add (Probe ... probes){
		ProbeSet set = new ProbeSet(this);
		set.set(probes);
		return set;
	}
	
	public ProbeSet resolve (Probe remove, ProbeContainer resolver) {
		ProbeSet set = new ProbeSet(this, remove.id);
		resolver.getResolves(set);
		return set;
	}
	
	public ProbeSet remove (Probe remove) {
		return new ProbeSet(this, remove.id);
	}
	
	public static interface ProbeContainer {
		public default int getResolves(ProbeSet set) {
			return 0;
		}
	}
}
