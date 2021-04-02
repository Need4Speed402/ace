package value.intrinsic;

import runtime.Effect;
import runtime.Runtime;
import value.Value;
import value.Value.CallReturn;
import value.ValueFunction;
import value.ValuePartial.Probe;
import value.intrinsic.Compare.Pair;
import value.resolver.Resolver;
import value.resolver.ResolverArgument;
import value.resolver.ResolverMutable;

public class Mutable{
	// init is the initial value that the mutable shall hold
	// set is the identifier the expose the set function
	// get is the identifier to expose the current mutable value
	public static final Value instance = new ValueFunction(init -> new CallReturn(new ValueFunction (set -> new CallReturn (new ValueFunction(get -> new CallReturn(new ValueFunction(handler -> {
		MutableProbe probe = new MutableProbe();
		
		CallReturn ret = handler.call(new ValueFunction(env -> new CallReturn(Compare.create(env,
			new Pair(set, new ValueFunction (setValue -> new CallReturn(setValue, new EffectSet(probe, setValue)))),
			new Pair(get, probe)
		))));

		return new CallReturn(ret.value, new EffectDeclare (probe, init), ret.effect);
	})))))));
	
	public static class MutableProbe extends Probe {
		@Override
		public String toString() {
			return Value.printHash("ProbeMutable", this.hashCode());
		}
	}
	
	public static class EffectSet implements Effect{
		public final MutableProbe probe;
		public final Value value;
		
		public EffectSet (MutableProbe probe, Value value) {
			this.probe = probe;
			this.value = value;
		}
		
		public void run(Runtime runtime) {
			
		}
		
		@Override
		public String toString() {
			return "Set " + this.probe + " = " + this.value;
		}
		
		@Override
		public CallReturn resolve(Resolver res) {
			Effect e;
			
			if (res instanceof ResolverMutable) {
				Value v = ((ResolverMutable) res).set(this.probe, res.resolveValue(this.value));
				
				if (v == null) {
					e = Effect.NO_EFFECT;
				}else {
					e = new EffectSet(this.probe, v);
				}
			}else {
				e = new EffectSet((MutableProbe) res.resolveValue(this.probe), res.resolveValue(this.value));
			}
			
			return new CallReturn(null, e);
		}
	}
	
	public static class EffectDeclare implements Effect {
		public final MutableProbe probe;
		public final Value value;
		
		public EffectDeclare (MutableProbe probe, Value value) {
			this.probe = probe;
			this.value = value;
		}
		
		public void run(Runtime runtime) {
			
		}
		
		@Override
		public String toString() {
			return "Declare " + this.probe + " = " + this.value;
		}
		
		@Override
		public CallReturn resolve(Resolver res) {
			Effect e;

			if (res instanceof ResolverMutable) {
				((ResolverMutable) res).put(this.probe, res.resolveValue(this.value));
				
				e = Effect.NO_EFFECT;
			}else {
				MutableProbe probe;
				
				if (res instanceof ResolverArgument) {
					probe = ((ResolverArgument) res).setMutable(this.probe);
				}else {
					probe = (MutableProbe) res.resolveValue(this.probe);
				}

				e = new EffectDeclare(probe, res.resolveValue(this.value));
			}

			return new CallReturn (null, e);
		}
	}
}
