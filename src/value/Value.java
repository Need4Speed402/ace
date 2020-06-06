package value;

import value.effect.Runtime;

public interface Value {
	public Value call (Value v);
	
	public default Value resolve (ValueProbe probe, Value value) {
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
	
	public interface Getter {
		public Value resolved (int value);
		
		public default Getter resolve (ValueProbe probe, Value value) {
			return this;
		}
	}
}
