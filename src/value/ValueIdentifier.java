package value;

import value.node.NodeIdentifier;

public class ValueIdentifier implements Value{
	public final static Value NULL = p -> ValueEffect.wrap(p, ValueIdentifier.NULL);
	
	private final int id;
	
	public ValueIdentifier (int id) {
		this.id = id;
	}
	
	@Override
	public Value call(Value v) {
		return NULL.call(v);
	}
	
	@Override
	public Value getID(Getter getter) {
		return getter.resolved(id);
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + NodeIdentifier.asString(id) + ")";
	}
}
