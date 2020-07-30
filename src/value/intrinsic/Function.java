package value.intrinsic;

import parser.ProbeSet;
import value.Value;
import value.Value.Getter;
import value.ValueFunction;
import value.ValuePartial.Probe;
import value.effect.Runtime;

public class Function{
	public static final Value instance = identv -> {
		return identv.getID(ident ->
			new ValueFunction(body -> new ValueFunction(arg ->
				body.call(new Env(arg, ident))
			, body))
		);
	};
	
	private static class Env implements Value {
		private final Value arg;
		private final int value;
		
		public Env (Value arg, int value) {
			this.arg = arg;
			this.value = value;
		}
		
		@Override
		public Value run(Runtime r) {
			return new Env(this.arg.run(r), value);
		}
		
		@Override
		public Value call(Value v) {
			return v.getID(new Arg(this.arg, v, this.value));
		}
		
		@Override
		public Value resolve(Probe probe, Value value) {
			Value v = this.arg.resolve(probe, value);
			
			if (v == this.arg) {
				return this;
			}else {
				return new Env(v, this.value);
			}
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			this.arg.getResolves(set);
		}
		
		@Override
		public String toString() {
			return super.toString() + " ? " + this.value + " -> " + this.arg;
		}
	}
	
	private static class Arg implements Getter {
		private final Value arg, env;
		private final int value;
		
		private Arg (Value arg, Value env, int value) {
			this.arg = arg;
			this.env = env;
			this.value = value;
		}
		
		@Override
		public Value resolved(int value) {
			if (this.value == value) {
				return this.arg;
			}else {
				return this.env;
			}
		}
		
		@Override
		public Getter resolve(Probe probe, Value value) {
			Value a = this.arg.resolve(probe, value);
			Value e = this.env.resolve(probe, value);
			
			if (a == this.arg & e == this.env) {
				return this;
			}else {
				return new Arg(a, e, this.value);
			}
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			this.arg.getResolves(set);
			this.env.getResolves(set);
		}
	}
}
