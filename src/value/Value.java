package value;

import value.ValuePartial.Probe;
import value.effect.Runtime;

public interface Value {
	public static final int DEFAULT_ID = 0;
	
	public Value call (Value v);
	
	public default Value resolve (Probe probe, Value value) {
		return this;
	}
	
	public default Value getID (Getter getter) {
		return getter.resolved(DEFAULT_ID);
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
	
	public interface Getter {
		public Value resolved (int value);
		
		public default Getter resolve (Probe probe, Value value) {
			return this;
		}
	}
}
