package value;

import value.ValuePartial.Probe;
import value.effect.Runtime;

public interface Value {
	public Value call (Value v);
	
	public default Value resolve (Resolver res) {
		return this;
	}
	
	public default Value getID (Getter getter) {
		return getter.resolved(0);
	}
	
	/*
	 * this function represents a hold of until I can figure out how I want
	 * to design the compiler. After the optimizer is done, the run function
	 * will be invoked and will run the optimized ast as if it were
	 * interpreted.
	 */
	public default Value run (Runtime r) {
		return this;
	}
	
	public interface Resolver {};
	
	public class ProbeResolver implements Resolver {
		public final Probe probe;
		private final Value value;
		
		public ProbeResolver (Probe probe, Value value) {
			this.probe = probe;
			this.value = value;
		}
		
		public Value use () {
			return this.value;
		}
	}
	
	public interface Getter {
		public Value resolved (int value);
		
		public default Getter resolve (Resolver r) {
			return this;
		}
	}
}
