package value;

import parser.ProbeSet;
import value.resolver.Resolver;

public interface Value extends ProbeSet.ProbeContainer {
	public static final int DEFAULT_ID = 0;
	
	public Value call (Value v);
	
	public default Value resolve (Resolver resolver) {
		return this;
	}
	
	public default Value getID (Getter getter) {
		return getter.resolved(DEFAULT_ID);
	}
	
	public interface Getter extends ProbeSet.ProbeContainer {
		public Value resolved (int value);
		
		public default Getter resolve (Resolver resolver) {
			return this;
		}
	}
}
