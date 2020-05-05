package value;

import value.effect.Effect;

public interface Value {
	public Value call (Value v);
	
	public default Value getID (Getter getter) {
		return getter.resolved(0);
	}
	
	public default Value resolve (ValueProbe probe, Value value) {
		return this;
	}
	
	public default Effect[] getEffects () {
		return Effect.NO_EFFECTS;
	}
	
	public interface Getter {
		public Value resolved (int value);
	}
}
