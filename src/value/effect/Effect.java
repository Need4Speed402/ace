package value.effect;

import value.Value.Resolver;

public interface Effect {
	public void run (Runtime runtime);
	
	public default Effect resolve (Resolver res) {
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
