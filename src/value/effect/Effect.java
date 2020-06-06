package value.effect;

import value.Value;
import value.ValueProbe;
import value.effect.Runtime;

public interface Effect {
	public void run (Runtime runtime);
	
	public default Effect resolve (ValueProbe probe, Value value) {
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
