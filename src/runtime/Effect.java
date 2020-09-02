package runtime;

import parser.ProbeSet;
import value.resolver.Resolver;

public interface Effect extends ProbeSet.ProbeContainer{
	public void run (Runtime runtime);
	
	public default void getResolves (ProbeSet set) {}
	
	public default Effect resolve (Resolver resolver) {
		return this;
	}
	
	public static final Effect NO_EFFECT = new Effect() {
		@Override
		public void run(Runtime runtime) {}
		
		@Override
		public String toString() {
			return "NO_EFFECT";
		}
	};
}
