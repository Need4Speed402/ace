package value.effect;

import value.Memory;
import value.Value;
import value.ValueProbe;

public class EffectGet implements Effect{
	private final Memory ref;
	private final ValueProbe probe;
	
	public EffectGet (Memory ref, ValueProbe probe) {
		this.ref = ref;
		this.probe = probe;
	}
	
	public Value run(Runtime runtime, Value root) {
		root = root.resolve(this.probe, this.ref.value);
		
		Effect.runAll(runtime, root);
		return root;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + this.probe.toString();
	}
}
