package value.intrinsic;

import java.util.HashMap;

import value.Value;
import value.ValueEffect;
import value.ValueFunction;
import value.ValuePartial.Probe;
import value.effect.EffectSet;
import value.effect.Runtime;
import value.effect.Runtime.Resolve;

public class Mutable implements Value {
	public static final Probe DUP_PROBE = new Probe();
	
	public static final Value instance = new ValueFunction(init -> {
		Probe probe = new Probe();
		
		return new ValueEffect(new Mutable(probe), new EffectSet(probe, init));
	});
	
	private final Probe probe;
	
	public Mutable (Probe probe) {
		this.probe = probe;
	}
	
	@Override
	public Value call(Value v) {
		return v.call(new Setter(this.probe))
		        .call(new Getter(this.probe));
	}
	
	@Override
	public Value resolve(Resolver res) {
		if (res instanceof Context) {
			return new Mutable (((Context) res).createMapping(this.probe));
		}
		
		return this;
	}
	
	private static class Setter implements Value{
		private final Probe probe;
		
		public Setter (Probe probe){
			this.probe = probe;
		}
		
		@Override
		public Value call(Value v) {
			return new ValueEffect(v, new EffectSet(this.probe, v));
		}
		
		@Override
		public Value resolve(Resolver res) {
			if (res instanceof Context) {
				return new Setter (((Context) res).createMapping(this.probe));
			}
			
			return this;
		}
	}
	
	private static class Getter implements Value {
		private final Probe probe;
		
		public Getter (Probe probe) {
			this.probe = probe;
		}
		
		@Override
		public Value call(Value v) {
			return this.probe;
		}
		
		@Override
		public Value resolve(Resolver res) {
			if (res instanceof Context) {
				return new Mutable.Getter (((Context) res).createMapping(this.probe));
			}
			
			return this;
		}
	}
	
	private static class MutableProbe extends Probe {
		private final Probe initial;
		
		public MutableProbe (Probe initial) {
			this.initial = initial;
		}
		
		@Override
		public Value run(Runtime r) {
			Resolve current = r.memory;
			
			while (current != null) {
				if (current.probe == this) {
					return current.value;
				}
				
				current = current.next;
			}
			
			Value v = this.initial.run(r);
			r.setResolve(this, v);
			return v;
		}
		
		@Override
		public String toString() {
			return super.toString() + "\n" + this.initial;
		}
	}
	
	public static class Context implements Resolver {
		private HashMap<Probe, MutableProbe> map = new HashMap<>();
		
		private Probe createMapping (Probe initial) {
			MutableProbe mapping = this.map.get(initial);
			
			if (mapping == null) {
				mapping = new MutableProbe(initial);
				this.map.put(initial, mapping);
			}else {
				System.out.println("reusing mapping");
			}
			
			return mapping;
		}
	}
}
