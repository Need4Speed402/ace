package value.intrinsic;

import runtime.Effect;
import runtime.Runtime;
import value.Value;
import value.Value.CallReturn;
import value.ValueFunction;
import value.resolver.Resolver;

public class Compare {
	public static final Value instance = new ValueFunction(a -> new CallReturn(new ValueFunction(b -> new CallReturn(new ValueFunction(x -> new CallReturn(new ValueFunction (y -> new CallReturn(create(a, y, b, x)))))))));

	public static Value create (Value base, Value def, Value comp, Value pass) {
		if (def == pass) return pass;
		
		int aa = base.getID();
		int bb = comp.getID();
		
		if (aa == -1 | bb == -1) {
			return new ValueCompare(base, comp, pass, def);
		}else if (aa == bb) {
			return pass;
		}else {
			return def;
		}
	}
	

	private static class ValueCompare implements Value {
		public final Value a, b, pass, fail;
		
		public ValueCompare (Value a, Value b, Value pass, Value fail) {
			this.a = a;
			this.b = b;
			this.pass = pass;
			this.fail = fail;
		}
		
		@Override
		public CallReturn call(Value v) {
			CallReturn pass = this.pass.call(v);
			CallReturn fail = this.fail.call(v);

			return new CallReturn(new ValueCompare(this.a, this.b, pass.value, fail.value), new EffectCompare(this.a, this.b, pass.effect, fail.effect));
		}
		
		@Override
		public Value resolve(Resolver resolver) {
			return Compare.create(
				this.a.resolve(resolver),
				this.fail.resolve(resolver),
				this.b.resolve(resolver),
				this.pass.resolve(resolver)
			);
		}
		
		@Override
		public int getID() {
			int aa = this.pass.getID();
			int bb = this.fail.getID();
			
			if (aa != bb | bb == -1) {
				return -1;
			}else {
				return aa;
			}
		}
		
		@Override
		public String toString() {
			return Value.print("Compare", this.a, this.b, this.pass, this.fail);
		}
	}

	private static class EffectCompare implements Effect {
		public final Value a, b;
		public final Effect pass, fail;
		
		public EffectCompare (Value a, Value b, Effect pass, Effect fail) {
			this.a = a;
			this.b = b;
			this.pass = pass;
			this.fail = fail;
		}
		
		@Override
		public Effect resolve(Resolver resolver) {
			Value a = this.a.resolve(resolver);
			Value b = this.b.resolve(resolver);
			int aa = a.getID();
			int bb = b.getID();
			
			Effect pass = this.pass.resolve(resolver);
			Effect fail = this.fail.resolve(resolver);
			
			if (pass == fail) {
				return pass;
			}else if (aa == -1 | bb == -1) {
				return new EffectCompare(
					a,
					b,
					pass,
					fail
				);
			}else if (aa == bb){
				return pass;
			}else {
				return fail;
			}
		}
		
		@Override
		public void run(Runtime runtime) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public String toString() {
			return Value.print("CompareEffects", this.a, this.b, this.pass, this.fail);
		}
	};
}

/*import java.util.ArrayList;

import runtime.Effect;
import runtime.Runtime;
import value.Value;
import value.Value.CallReturn;
import value.ValueFunction;
import value.intrinsic.Assign.IdentityCache;
import value.resolver.Resolver;

public class Compare<K>{
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
		return new Compare(base, def, unbound, unboundLength, map);
	}

	public static final Value instance = new ValueFunction(a -> new CallReturn(new ValueFunction(b -> new CallReturn(new ValueFunction(x -> new CallReturn(new ValueFunction (y -> new CallReturn(create(a, y, b, x)))))))));
	
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
	
	protected final Value base;
	protected final K def;
	protected final K[] values;
	protected final Value[] keys;
	protected final int[] map;
	
	private Compare (Value base, K def, int[] map, Value[] keys, K[] values) {
		this.base = base;
		this.def = def;
		this.map = map;
		this.keys = keys;
		this.values = values;
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
	
	public K resolve(int[] map, Value[] keys, K[] values, Resolver resolver, GenericResolver<K> genericResolver) {
		for (int i = 0; i < this.map.length; i++) {
			map[i] = this.map[i];
			values[i] = this.values[i];
		}
		
		ArrayList<Value> unbound = new ArrayList<>();
		ArrayList<K> unboundValues = new ArrayList<>();
		
		//the keys array contains items that we don't know the id of yet.
		// we're going to try to resolve these first
		for (int i = 0; i < this.keys.length; i++) {
			Value v = this.keys[i].resolve(resolver);
			int id = v.getID() + 1;
			
			if (id == -1) {
				unbound.add(v);
				unboundValues.add(this.values[this.map.length + i]);
			}else{
				int current = id % map.length;

				while (true) {
					if (map[current] == 0) {
						map[current] = id;
						values[current] = this.values[this.map.length + i];
						break;
					}else {
						current = (current + 1) % map.length;
					}
				}
			}
		}

		{
			int id = base.getID() + 1;
			
			//we know what the id is for the thing we want to compare against. Look it up into the hashmap.
			if (id != 0) {
				int current = id % map.length;
				
				while (true) {
					if (map[current] == id) {
						return values[current];
					}else if (map[current] == 0) {
						break;
					}else {
						current = (current + 1) % map.length;
					}
				}
				
				// if all the items we care about are bound, then if we still haven't found anything, we return the default value.
				if (unbound.size() == 0) {
					return def;
				}
			}
		}
		
		Value[] unboundComplete = new Value[unbound.size()];
		
		for (int i = 0; i < unbound.size(); i++) {
			unboundComplete[i] = unbound.get(i);
			values[this.map.length + i] = unboundValues.get(i);
		}
		
		return null;

		if (def instanceof Compare && ((Compare) def).base == base) {
			return create(base, ((Compare) def).def, Pair.join(this.getPairs(), ((Compare) def).getPairs()));
		}
		
		return new MapCompare(base, def, newUnbound, boundLength, newMap);
	
	
	@Override
	public String toString() {
		Pair[] pairs = this.getPairs();
		Object[] elements = new Object[2 + pairs.length];
		elements[0] = this.base;
		elements[1] = this.def;
		for (int i = 0; i < pairs.length; i++) elements[i + 2] = pairs[i];
				
		return Value.print("Compare", elements);
	}
	
	private static class ValueCompare implements Value{
		private final Compare<Value> map;

		private ValueCompare (Value base, Value def, int[] map, Value[] keys, Value[] values) {
			this.map = new Compare<Value>(base, def, map, keys, values);
		}
	
		@Override
		public CallReturn call(Value v) {
			Value[] values = new Value[map.values.length];
			Effect[] effects = new Effect[map.values.length];
			CallReturn def = map.def.call(v);
			
			for (int i = 0; i < map.values.length; i++) {
				CallReturn ret = map.values[i].call(v);
				values[i] = ret.value;
				effects[i] = ret.effect;
			}
			
			return new CallReturn(new ValueCompare(map.base, def.value, map.map, map.keys, values), new EffectCompare(map.base, def.effect, map.map, map.keys, effects));
		}

		@Override
		public int getID() {
			int running = -1;
			for (int i = 0; i < map.values.length; i++) {
				int id = map.values[i].getID();
				
				if (id == -1) return -1;
				if (running == -1) running = id;
				else if (running != id) return -1;
			}
			
			return running;
		}
		
		@Override
		public Value resolve(Resolver resolver) {
			int[] map = new int[this.map.map.length];
			Value[] values = new Value[this.map.values.length];
			Value v = this.map.resolve(map, values, resolver, s -> s.resolve(resolver));
			if (v != null) return v;
			return new ValueCompare(this.base.resolve(resolver), this.def, map, )
		}
	}

	private static class EffectCompare implements Effect{
		private final Compare<Effect> map;

		private EffectCompare (Value base, Effect def, int[] map, Value[] keys, Effect[] values) {
			this.map = new Compare<Effect>(base, def, map, keys, values);
		}

		@Override
		public void run(Runtime runtime) {
			throw new RuntimeException ("not sure what to do here");
		}
	}
	
	private static interface GenericResolver<K> {
		public K resolve(K value);
	}
}*/
