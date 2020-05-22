package value;

public interface Value {
	public Value call (Value v);
	
	public default Value resolve (ValueProbe probe, Value value) {
		return this;
	}
	
	public default Value getID (Getter getter) {
		return getter.resolved(0);
	}
	
	public interface Getter {
		public Value resolved (int value);
		
		public default Getter resolve (ValueProbe probe, Value value) {
			return this;
		}
	}
}
