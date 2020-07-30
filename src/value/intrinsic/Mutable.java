package value.intrinsic;

import parser.ProbeSet;
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
		return v.call(new ValueFunction(p -> new ValueEffect(p, new EffectSet(this.probe, p)), this.probe))
		        .call(new ValueFunction(p -> this.probe, this.probe));
	}
	
	@Override
	public Value resolve(Probe probe, Value value) {
		if (this.probe == probe) {
			return new Mutable((Probe) value);
		}
		
		return this;
	}
	
	@Override
	public void getResolves(ProbeSet set) {
		set.set(this.probe);
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
			Value v = this.value.resolve(probe, value);
			
			if (v == this.value & this.probe != probe) {
				return this;
			}else {
				return new EffectSet(this.probe != probe ? this.probe : (Probe) value, v);
			}
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			set.set(this.probe);
			this.value.getResolves(set);
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
			return new EffectDeclare(
				this.probe == probe ? (Probe) value : this.probe,
				this.value.resolve(probe, value)
			);
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			set.set(this.probe);
			this.value.getResolves(set);
		}
	}
}
