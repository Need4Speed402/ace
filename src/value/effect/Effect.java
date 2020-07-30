package value.effect;

import parser.ProbeSet;
import value.Value;
import value.ValuePartial.Probe;

public interface Effect extends ProbeSet.Resolver{
	public void run (Runtime runtime);
	
	public default void getResolves (ProbeSet set) {}
	
	public default Effect resolve (Probe probe, Value value) {
		return this;
	}
	
	public static final Effect NO_EFFECT = new Effect() {
		@Override
		public void run(Runtime runtime) {}
		
		@Override
		public String toString() {
			return "NO_EFFECT";
		}
	};
}
