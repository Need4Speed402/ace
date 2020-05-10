package value;

public interface Value {
	public Value call (Value v);
	
	public default Value getID (Getter getter) {
		return getter.resolved(0);
	}
	
	public default Value resolve (ValueProbe probe, Value value) {
		return this;
	}
	
	public default boolean canCreateEffects () {
		return false;
	}
	
	public interface Getter {
		public Value resolved (int value);
	}
}
