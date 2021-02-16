package value.intrinsic;

import value.Value;
import value.ValueFunction;
import value.intrinsic.Assign.IdentityCache;
import value.resolver.Resolver;

public abstract class Compare implements Value{
	public static Value create (Value base, Value def, Value comp, Value pass) {
		if (def == pass) return pass;
		
		int aa = base.getID();
		int bb = comp.getID();
		
		if (aa == -1 | bb == -1) {
			if (def instanceof Compare) {
				if (((Compare) def).base == comp) {
					Value temp = comp;
					comp = base;
					base = temp;
				}
				
				if (((Compare) def).base == base) {
					return create(base, ((Compare) def).def, Pair.join(new Pair[] {new Pair(comp, pass)}, ((Compare) def).getPairs()));
				}
			}
			
			return new SingleCompare(base, def, comp, pass);
		}else if (aa == bb) {
			return pass;
		}else {
			return def;
		}
	}
	
	public static Value create (Value base, Value def, Pair ... pairs) {
		Value[] unbound = new Value[pairs.length * 3];
		Value[] bound = new Value[pairs.length * 2];
		int[] map = new int[pairs.length * 2];

		int unboundLength = 0;
		
		for (int i = 0; i < pairs.length; i++) {
			int id = pairs[i].key.getID() + 1;
			
			if (id == 0) {
				unbound[unboundLength++] = pairs[i].key;
				unbound[unboundLength++] = pairs[i].value;
			}else {
				int current = id % map.length;
				
				while (true) {
					if (map[current] == 0) {
						map[current] = id;
						bound[current] = pairs[i].value;
						break;
					}else if (map[current] == id) {
						// if we found a duplicate value, discard it.
						break;
					} else {
						current = (current + 1) % map.length;
					}
				}
			}
		}
		
		for (int i = 0; i < bound.length; i++) unbound[i + unboundLength] = bound[i];
		return new MapCompare(base, def, unbound, unboundLength, map);
	}

	protected final Value base, def;

	private Compare (Value base, Value def) {
		this.base = base;
		this.def = def;
	}
	
	public abstract Pair[] getPairs ();
	
	public static final Value instance = new ValueFunction(a -> new ValueFunction(b -> new ValueFunction(x -> new ValueFunction (y -> create(a, y, b, x)))));
	
	public static class Pair {
		public final Value key, value;
		
		public Pair (Value key, Value value) {
			this.key = key;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return Value.print("Pair", this.key, this.value);
		}
		
		public static Pair[] join (Pair[] ... pairs) {
			int len = 0;
			for (int i = 0; i < pairs.length; i++) len += pairs[i].length;
			Pair[] joined = new Pair[len];
			int o = 0;
			for (int i = 0; i < pairs.length; i++) {
				for (int ii = 0; ii < pairs[i].length; ii++) {
					joined[o++] = pairs[i][ii];
				}
			}
			
			return joined;
		}
	}
	
	public static class SingleCompare extends Compare {
		private final Value comp, pass;
		
		public SingleCompare (Value base, Value def, Value comp, Value pass) {
			super (base, def);
			this.comp = comp;
			this.pass = pass;
		}
		
		@Override
		public Pair[] getPairs() {
			return new Pair[] { new Pair(this.comp, this.pass) };
		}

		@Override
		public Value resolve(Resolver resolver) {
			return create(
				resolver.cache(this.base),
				resolver.cache(this.def),
				resolver.cache(this.comp),
				resolver.cache(this.pass)
			);
		}
		
		@Override
		public Value call(Value arg) {
			return new SingleCompare(this.base, this.def.call(arg), this.comp, this.pass.call(arg));
		}
		
		@Override
		public int getID() {
			int xx = this.pass.getID();
			int yy = this.def.getID();
			
			if (xx == -1 | yy == -1 | xx != yy) {
				return -1;
			}else {
				return xx;
			}
		}
		
		@Override
		public String toString() {
			return Value.print("Compare", this.base, this.def, new Pair(this.comp, this.pass));
		}
	}
	
	//as an optimization, the compiler will optimize patters of chained together compares (which are quite common)
	// and turn it into an efficient hashmap lookup.
	private static class MapCompare extends Compare {
		//the bound array has two parts: unbound values (values of which we don't know their id yet)
		// and the rest of the array is going to contain bound values. The bound array contains the values
		// that the keys would map to when looking up in the hashmap. The
		//the bound array is going contain values that we are comparing against
		// when we don't know what their actual id is yet.
		//the boundIndex int is specifies the start index of their the bound values start. Unbound values always start at 0.
		//this is done to prevent heap fragmentation and cache performance.
		private final Value[] bound;
		private final int boundIndex;
		private final int[] map;
		
		public MapCompare (Value base, Value def, Value[] bound, int boundIndex, int[] map) {
			super(base, def);
			this.bound = bound;
			this.boundIndex = boundIndex;
			this.map = map;
		}
		
		@Override
		public Pair[] getPairs() {
			int count = this.boundIndex / 2;
			for (int i = 0; i < this.map.length; i++) if (this.map[i] != 0) count++;
			Pair[] pairs = new Pair[count];
			
			for (int i = 0; i < this.map.length; i++) {
				int id = this.map[i];
				if (id == 0) continue;
				pairs[--count] = new Pair(new IdentityCache(id - 1), this.bound[this.boundIndex + i]);
			}
			
			for (int i = 0; i < this.boundIndex; i += 2) {
				pairs[--count] = new Pair(this.bound[i], this.bound[i + 1]);
			}
			
			return pairs;
		}
		
		@Override
		public Value resolve(Resolver resolver) {
			Value[] newUnbound;
			int[] newMap = new int[this.map.length];
			int boundLength = 0;
			
			for (int i = 0; i < this.map.length; i++) newMap[i] = this.map[i];
			if (this.boundIndex == 0) {
				newUnbound = new Value[this.map.length];
				
				for (int i = 0; i < this.map.length; i++) {
					Value b = this.bound[i];
					if (b == null) continue;
					newUnbound[i] = resolver.cache(b);
				}
			}else {
				newUnbound = new Value[this.bound.length];
				Value[] newBound = new Value[this.map.length];

				for (int i = 0; i < this.boundIndex; i += 2) {
					Value key = resolver.cache(this.bound[i]);
					int id = key.getID() + 1;
					
					if (id == 0) {
						// we still don't know what the value is, pass it on.
						newUnbound[boundLength++] = key;
						newUnbound[boundLength++] = resolver.cache(this.bound[i + 1]);
					}else{
						// we now know the id of this value, move it from the unbound portion
						// into the bound portion and add an entry into the hash set.
						
						int current = id % newMap.length;
						while (true) {
							if (newMap[i] == 0) {
								newMap[i] = id;
								newBound[i] = this.bound[i + 1];
								break;
							}else {
								current = (current + 1) % newMap.length;
							}
						}
					}
				}

				for (int i = 0; i < newBound.length; i++) {
					Value v = newBound[i];
					if (v == null) v = this.bound[this.boundIndex + i];
					if (v != null) v = resolver.cache(v);
					newUnbound[i + boundLength] = v;
				}
			}
			
			Value base = resolver.cache(this.base);
			Value def = resolver.cache(this.def);
			
			{
				int id = base.getID() + 1;
				
				//we know what the id is for the thing we want to compare against. Look it up into the hashmap.
				if (id != 0) {
					int current = id % newMap.length;
					
					while (true) {
						if (newMap[current] == id) {
							return newUnbound[current + boundLength];
						}else if (newMap[current] == 0) {
							break;
						}else {
							current = (current + 1) % newMap.length;
						}
					}
					
					// if all the items we care about are bound, then if we still haven't found anything, we return the default value.
					if (boundLength == 0) {
						return def;
					}
				}
			}
			
			if (def instanceof Compare && ((Compare) def).base == base) {
				return create(base, ((Compare) def).def, Pair.join(this.getPairs(), ((Compare) def).getPairs()));
			}
			
			return new MapCompare(base, def, newUnbound, boundLength, newMap);
		}
		
		@Override
		public int getID() {
			int running = -1;
			for (int i = 0; i < this.boundIndex; i += 2) {
				int id = this.bound[i + 1].getID();
				
				if (id == -1) return -1;
				if (running == -1) running = id;
				else if (running != id) return -1;
			}
			
			for (int i = this.boundIndex; i < this.bound.length; i++) {
				if (this.bound[i] == null) continue;
				int id = this.bound[i].getID();
				
				if (id == -1) return -1;
				if (running == -1) running = id;
				else if (running != id) return -1;
			}
			
			return running;
		}
		
		@Override
		public Value call(Value v) {
			Value[] newBound = new Value[this.bound.length];
			
			for (int i = 0; i < this.boundIndex; i += 2) {
				newBound[i] = this.bound[i];
				newBound[i + 1] = this.bound[i + 1].call(v);
			}
			
			for (int i = this.boundIndex; i < this.bound.length; i++) {
				newBound[i] = this.bound[i].call(v);
			}
			
			return new MapCompare(this.base, this.def, newBound, this.boundIndex, this.map);
		}
		
		@Override
		public String toString() {
			Pair[] pairs = this.getPairs();
			Object[] elements = new Object[2 + pairs.length];
			elements[0] = this.base;
			elements[1] = this.def;
			for (int i = 0; i < pairs.length; i++) elements[i + 2] = pairs[i];
					
			return Value.print("Compare", elements);
		}
	}
}
