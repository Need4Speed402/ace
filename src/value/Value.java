package value;

import parser.resolver.Resolver;

public interface Value {
	public static final Value NULL = p -> Value.NULL;
	
	public Value call (Value v);
	
	public default Value call (String s) {
		return this.call(new ValueIdentifier(s, Resolver.NULL));
	}
	
	public static boolean compare(Value v, String identifier){
		if (v instanceof ValueIdentifier) {
			return ((ValueIdentifier) v).id == identifier;
		}
		
		return false;
	}
}
