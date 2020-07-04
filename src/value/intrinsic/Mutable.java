package value.intrinsic;

import value.Value;
import value.ValueEffect;
import value.ValueFunction;
import value.ValuePartial.Probe;
import value.effect.Effect;
import value.effect.Runtime;

public class Mutable implements Value {
	public static final Probe DUP_PROBE = new Probe();
	
	public static final Value instance = new ValueFunction(init -> {
		Probe probe = new Probe();
		
		return new ValueEffect(new Mutable(probe), new EffectDeclare(probe, init));
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
	public Value resolve(Probe probe, Value value) {
		if (this.probe == probe) {
			return new Mutable((Probe) value);
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
		public Value resolve(Probe probe, Value value) {
			if (this.probe == probe) {
				return new Setter((Probe) value);
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
		public Value resolve(Probe probe, Value value) {
			if (this.probe == probe) {
				return new Mutable.Getter((Probe) value);
			}
			
			return this;
		}
	}
	
	public static class EffectSet implements Effect{
		public final Probe probe;
		public final Value value;
		
		public EffectSet (Probe probe, Value value) {
			this.probe = probe;
			this.value = value;
		}
		
		public void run(Runtime runtime) {
			runtime.set(this.probe, this.value.run(runtime));
		}
		
		@Override
		public String toString() {
			return "Set " + this.probe + " = " + this.value;
		}
		
		@Override
		public Effect resolve(Probe probe, Value value) {
			if (this.probe == probe) {
				return new EffectSet((Probe) value, this.value);
			}else {
				return new EffectSet(this.probe, this.value.resolve(probe, value));
			}
		}
	}
	
	public static class EffectDeclare implements Effect {
		public final Probe probe;
		public final Value value;
		
		public EffectDeclare (Probe probe, Value value) {
			this.probe = probe;
			this.value = value;
		}
		
		public void run(Runtime runtime) {
			runtime.declare(this.probe, this.value.run(runtime));
		}
		
		@Override
		public String toString() {
			return "Declare " + this.probe + " = " + this.value;
		}
		
		@Override
		public Effect resolve(Probe probe, Value value) {
			if (this.probe == probe) {
				return new EffectDeclare((Probe) value, this.value);
			}else {
				return new EffectDeclare(this.probe, this.value.resolve(probe, value));
			}
		}
	}
}
