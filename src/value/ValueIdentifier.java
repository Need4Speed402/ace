package value;

public class ValueIdentifier implements Value {
	public final String id;
	public final Value value;
	
	public ValueIdentifier (String id, Value value) {
		this.id = id;
		this.value = value;
	}
	
	@Override
	public Value call(Value p) {
		return this.value.call(p);
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + this.id + "]";
	}
}
