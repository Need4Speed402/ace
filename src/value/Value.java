package value;

import value.effect.Effect;

public interface Value {
	public Value call (Value v);
	
	public default Value resolve (ValueProbe probe, Value value) {
		return this;
	}
	
	public default Value getID (Getter getter) {
		return getter.resolved(0);
	}
	
	public default Effect getEffect () {
		return Effect.NO_EFFECT;
	}
	
	public interface Getter {
		public Value resolved (int value);
		
		public default Getter resolve (ValueProbe probe, Value value) {
			return this;
		}
	}
}
