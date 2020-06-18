package value.intrinsic;

import value.Value;
import value.ValueFunction;

public class Assign implements Value{
	public static final Value instance = new ValueFunction(name ->
		new ValueFunction(value -> new Assign(name, value))
	);
	
	private final Value name, value;
	
	public Assign (Value name, Value value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Value resolve(Resolver res) {
		return new Assign(this.name.resolve(res), this.value.resolve(res));
	}
	
	@Override
	public Value call (Value v) {
		return this.value.call(v);
	}
	
	@Override
	public Value getID (Getter getter) {
		return this.name.getID(getter);
	}
	
	@Override
	public String toString() {
		return "Assignment(" + this.name.toString() + ") -> " + this.value.toString();
	}
}
