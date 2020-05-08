package value;

public class ValueAssign implements Value {
	private final Value name, value;
	
	public ValueAssign (Value name, Value value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		return new ValueAssign(this.name.resolve(probe, value), this.value.resolve(probe, value));
	}
	
	@Override
	public Value call (Value v) {
		return value.call(v);
	}
	
	@Override
	public Value getID (Getter getter) {
		return name.getID(getter);
	}
	
	@Override
	public String toString() {
		return "Assignment(" + name.toString() + ") -> " + value.toString();
	}
}
