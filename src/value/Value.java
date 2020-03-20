package value;

import parser.Promise;

public interface Value {
	public static final Promise<Integer> DEFAULT_ID = new Promise<Integer>(0);
	
	public Value call (Value v);
	
	public default Promise<Integer> getID () {
		return DEFAULT_ID;
	}
}
