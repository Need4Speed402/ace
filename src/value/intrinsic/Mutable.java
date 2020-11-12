package value.intrinsic;

import runtime.Effect;
import runtime.Runtime;
import value.Value;
import value.ValueEffect;
import value.ValueFunction;
import value.ValuePartial.Probe;
import value.resolver.Resolver;
import value.resolver.ResolverFunctionBody;
import value.resolver.ResolverMutable;

public class Mutable implements Value {
	public static final Value instance = new ValueFunction(init -> new Mutable(init));
	
	private final Value init;
	
	public Mutable (Value init) {
		this.init = init;
	}
	
	@Override
	public Value call(Value v) {
		Probe probe = new MutableProbe();
		
		return ValueEffect.create(
			v.call(new ValueFunction(p -> ValueEffect.create(p, new EffectSet(probe, p))))
			 .call(new ValueFunction(p -> probe)),
			new EffectDeclare(probe, init)
		);
	}
	
	@Override
	public Value resolve(Resolver res) {
		Value init = this.init.resolve(res);
		
		if (init == this.init) {
			return this;
		}else {
			return new Mutable(init);
		}
	}
	
	@Override
	public String toString() {
		return "MutableDeclare(" + this.init + ")";
	}
	
	@Override
	public int complexity() {
		return this.init.complexity() + 1;
	}
	
	public static class MutableProbe extends Probe {
		@Override
		public int complexity() {
			return 0;
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
			runtime.set(this.probe, this.value);
		}
		
		@Override
		public String toString() {
			return "Set " + this.probe + " = " + this.value;
		}
		
		@Override
		public Effect resolve(Resolver res) {
			if (res instanceof ResolverFunctionBody) {
				Probe p = ((ResolverFunctionBody) res).get(this.probe);
				Value v = this.value.resolve(res);
				
				if (v == this.value & p == this.probe) {
					return this;
				}else {
					return new EffectSet(p, v);
				}
			}else if (res instanceof ResolverMutable) {
				Value v = ((ResolverMutable) res).set(this.probe, this.value.resolve(res));
				
				if (v == null) {
					return Effect.NO_EFFECT;
				}else if (v == this.value) {
					return this;
				}else {
					return new EffectSet(this.probe, value);
				}
			}else {
				Value v = this.value.resolve(res);
				
				if (v == this.value) {
					return this;
				}else {
					return new EffectSet(this.probe, v);
				}
			}
		}
		
		@Override
		public int complexity() {
			return this.value.complexity() + 1;
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
			runtime.declare(this.probe, this.value);
		}
		
		@Override
		public String toString() {
			return "Declare " + this.probe + " = " + this.value;
		}
		
		@Override
		public Effect resolve(Resolver res) {
			if (res instanceof ResolverFunctionBody) {
				Probe p = ((ResolverFunctionBody) res).set(this.probe);
				Value v = this.value.resolve(res);
				
				if (v == this.value & p == this.probe) {
					return this;
				}else {
					return new EffectDeclare(p, v);
				}
			}else if (res instanceof ResolverMutable) {
				((ResolverMutable) res).put(this.probe, this.value.resolve(res));
				
				return Effect.NO_EFFECT;
			}else {
				Value v = this.value.resolve(res);
				
				if (v == this.value) {
					return this;
				}else {
					return new EffectDeclare(this.probe, v);
				}
			}
		}
		
		@Override
		public int complexity() {
			return this.value.complexity() + 1;
		}
	}
}
