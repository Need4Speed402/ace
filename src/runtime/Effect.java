package runtime;

import value.Value.CallReturn;
import value.resolver.Resolver;
import value.resolver.Resolver.Resolvable;

public interface Effect extends Resolvable{
	public void run (Runtime runtime);
	
	@Override
	public default CallReturn resolve(Resolver resolver) {
		return new CallReturn (null, this);
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
