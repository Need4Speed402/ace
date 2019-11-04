package value;

public class ValueIdentifier implements Value {
	public final String id;
	private final Value environment;
	
	public ValueIdentifier (String id, Value environment) {
		this.id = id;
		this.environment = environment;
	}
	
	public Value getReference (){
		return this.environment.call(new ValueIdentifier(this.id, Value.NULL));
	}
	
	@Override
	public Value call(Value p) {
		return this.getReference().call(p);
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + this.id + "]";
	}
}
