package value.effect;

import value.Value;
import value.Value.Resolver;
import value.ValuePartial.Probe;

public class EffectSet implements Effect{
	private final Value value;
	private final Probe probe;
	
	public EffectSet (Probe probe, Value value) {
		this.probe = probe;
		this.value = value;
	}
	
	public void run(Runtime runtime) {
		runtime.setResolve(this.probe, this.value.run(runtime));
	}
	
	@Override
	public Effect resolve(Resolver res) {
		return new EffectSet(this.probe, this.value.resolve(res));
	}
	
	@Override
	public String toString() {
		return "Set " + this.probe + " = " + this.value;
	}
}
