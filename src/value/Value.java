package value;

import value.resolver.Resolver;

public interface Value {
	public static final int DEFAULT_ID = 0;
	
	public Value call (Value v);
	
	public default Value resolve (Resolver resolver) {
		return this;
	}
	
	public default Value getID (Getter getter) {
		return getter.resolved(this, DEFAULT_ID);
	}
	
	public default int complexity() {
		return 0;
	}
	
	public interface Getter{
		public Value resolved (Value parent, int value);
		
		public default Getter resolve (Resolver resolver) {
			return this;
		}
		
		public default String toString (Value ident) {
			return null;
		}
		
		public default int complexity() {
			return 0;
		}
	}
	
	public static int add (int ... nums) {
		int add = 0;
		for (int i : nums) add += i;
		if (add != 0) add += nums.length - 1;
		return add;
	}
}
