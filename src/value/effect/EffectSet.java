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
	
	public void run(Runtime runtime, Value root) {
		runtime.setResolve(this.probe, this.value);
	}
	
	@Override
	public String toString() {
		return "Set " + this.probe + " = " + this.value;
	}
}
