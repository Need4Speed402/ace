package value.effect;

import value.Value;
import value.ValueProbe;

public interface Effect {
	public void run (Runtime runtime, Value root);
	
	public default Effect resolve (ValueProbe probe, Value value) {
		return this;
	}
}
