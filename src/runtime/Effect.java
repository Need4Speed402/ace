package runtime;

import value.resolver.Resolver;

public interface Effect{
	public void run (Runtime runtime);
	
	public default Effect resolve (Resolver resolver) {
		return this;
	}
	
	public default int complexity () {
		return 0;
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
