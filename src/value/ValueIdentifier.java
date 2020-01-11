package value;

public class ValueIdentifier implements Value {
	public static final String CONTINUE = "`";
	public static final String JOIN = "``";
	
	public final String name;
	public final Value value;
	
	public ValueIdentifier (String id, Value value) {
		this.name = id;
		this.value = value;
	}
	
	@Override
	public Value call(Value p) {
		return this.value.call(p);
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + this.name + "]";
	}
}
