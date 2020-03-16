package value;

import value.Value;

public class Delegate implements Value {
	private Value cache;
	private final Loader loader;
	
	protected Delegate (Loader loader) {
		this.loader = loader;
	}
	
	private Value get () {
		if (this.cache == null) this.cache = this.loader.load();
		return this.cache;
	}
	
	@Override
	public Value call(Value environment) {
		return this.get().call(environment);
	}
	
	@Override
	public String toString() {
		return this.get().toString();
	}
	
	public interface Loader {
		public Value load();
	}
}
