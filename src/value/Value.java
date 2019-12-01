package value;

import parser.resolver.Resolver;

public interface Value {
	public static final Value NULL = p -> Value.NULL;
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	
	public Value call (Value v);
	
	public default Value call (String s) {
		return this.call(new ValueIdentifier(s, Resolver.NULL));
	}
	
	public static boolean compare(Value v, String identifier){
		if (v instanceof ValueIdentifier) {
			return ((ValueIdentifier) v).name == identifier;
		}
		
		return false;
	}
	
	public static boolean compare (Value v1, Value v2) {
		return
			v1 instanceof ValueIdentifier &&
			v2 instanceof ValueIdentifier &&
			((ValueIdentifier) v1).name == ((ValueIdentifier) v2).name;
	}
}
