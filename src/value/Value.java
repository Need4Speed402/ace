package value;

public interface Value {
	public Value call (Value v);
	
	public default int getID () {
		return 0;
	}
}
