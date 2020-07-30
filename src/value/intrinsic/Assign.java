package value.intrinsic;

import parser.ProbeSet;
import value.Value;
import value.ValueFunction;
import value.ValuePartial.Probe;
import value.effect.Runtime;

public class Assign implements Value{
	public static final Value instance = new ValueFunction(name ->
		new ValueFunction(value -> new Assign(name, value), name)
	);
	
	private final Value name, value;
	
	public Assign (Value name, Value value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public void getResolves(ProbeSet set) {
		this.name.getResolves(set);
		this.value.getResolves(set);
	}
	
	@Override
	public Value resolve(Probe probe, Value value) {
		Value n = this.name.resolve(probe, value);
		Value v = this.value.resolve(probe, value);
		
		if (n == this.name & v == this.value) {
			return this;
		}else {
			return new Assign(n, v);
		}
	}
	
	@Override
	public Value run(Runtime r) {
		return new Assign(this.name.run(r), this.value.run(r));
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
