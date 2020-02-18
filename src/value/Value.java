package value;

public interface Value {
	public Value call (Value v);
	
	public default String getName () {
		return "";
	}
}
