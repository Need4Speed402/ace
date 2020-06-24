package value.intrinsic;

import java.util.HashMap;

import value.Value;
import value.ValueEffect;
import value.ValuePartial.Probe;
import value.effect.Effect;
import value.effect.EffectQueue;
import value.effect.Runtime;
import value.effect.Runtime.Resolve;

public class Mutable implements Value {
	public static final Probe DUP_PROBE = new Probe();
	
	public static final Value instance = init -> {
		Probe probe = new Probe();
		
		return new ValueEffect(new Mutable(probe), new EffectSet(probe, init));
	};
	
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
	
	public static class EffectSet implements Effect{
		private final Value value;
		private final Probe probe;
		
		public EffectSet (Probe probe, Value value) {
			this.probe = probe;
			this.value = value;
		}
		
		public void run(Runtime runtime) {
			runtime.setResolve(this.probe, this.value.run(runtime));
		}
		
		@Override
		public Effect resolve(Resolver res) {
			Effect t = new EffectSet(this.probe, this.value.resolve(res));
			
			if (res instanceof Mutable.Context) {
				return new EffectQueue(t, new EffectSet(((Mutable.Context) res).createMapping(this.probe), this.probe));
			}else {
				return t;
			}
		}
		
		@Override
		public String toString() {
			return "Set " + this.probe + " = " + this.value;
		}
	}
	
	public static class Context implements Resolver {
		private HashMap<Probe, MutableProbe> map = new HashMap<>();
		
		private Probe createMapping (Probe initial) {
			MutableProbe mapping = this.map.get(initial);
			
			if (mapping == null) {
				mapping = new MutableProbe(initial);
				this.map.put(initial, mapping);
			}
			
			return mapping;
		}
	}
}
