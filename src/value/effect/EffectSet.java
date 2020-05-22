package value.effect;

import value.Value;
import value.ValueProbe;

public class EffectSet implements Effect{
	private final Value value;
	private final ValueProbe probe;
	
	public EffectSet (ValueProbe probe, Value value) {
		this.probe = probe;
		this.value = value;
	}
	
	public void run(Runtime runtime) {
		runtime.setResolve(this.probe, this.value);
	}
	
	@Override
	public Effect resolve(ValueProbe probe, Value value) {
		return new EffectSet(this.probe, this.value.resolve(probe, value));
	}
	
	@Override
	public String toString() {
		return "Set " + this.probe + " = " + this.value;
	}
}
