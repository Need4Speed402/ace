package value.node;

import value.Value;

public class NodeDelegate implements Node {
	private Node cache;
	private final Loader loader;
	
	protected NodeDelegate (Loader loader) {
		this.loader = loader;
	}
	
	private Node get () {
		if (this.cache == null) this.cache = this.loader.load();
		return this.cache;
	}
	
	@Override
	public Value run(Value environment) {
		return this.get().run(environment);
	}
	
	@Override
	public String toString() {
		return this.get().toString();
	}
	
	public interface Loader {
		public Node load();
	}
}
