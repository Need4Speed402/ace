package value.intrinsic;

import value.Value;
import value.ValueFunction;
import value.resolver.Resolver;

public class Assign implements Value{
	public static final Value instance = new ValueFunction(name -> new CallReturn(new ValueFunction(value -> {
		// 'name' is the identifier used to derive what this value's identity should be
		// 'value' is the base value on which to apply a new identity.
		//Every value in ace has two parts, the behavior it will exhibit when it is called,
		// and what its identity is. The assign operator lets you choose what both behaviors
		// will be individually.
		return new CallReturn(create(name, value));
	})));
	
	public static Value create (Value name, Value value) {
		if (name == value) return name;
		
		if (name instanceof Assign) name = ((Assign) name).name;
		if (value instanceof Assign) value = ((Assign) value).value;
		
		return new Assign(name, value);
	}
	
	private final Value name, value;
	
	private Assign (Value name, Value value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public CallReturn resolve(Resolver res) {
		return new CallReturn(create(res.resolveValue(this.name), res.resolveValue(this.value)));
	}
	
	@Override
	public CallReturn call (Value v) {
		return this.value.call(v);
	}
	
	@Override
	public int getID () {
		return this.name.getID();
	}
	
	@Override
	public String toString() {
		return Value.print("Assign", this.name, this.value);
	}
}
