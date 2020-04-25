package value.effect;

import value.Memory;
import value.Value;

public class EffectSet implements Effect{
	private final Memory ref;
	private final Value value;
	
	public EffectSet (Memory ref, Value value) {
		this.ref = ref;
		this.value = value;
	}
	
	public Value run(Runtime runtime, Value root) {
		ref.value = this.value;
		return root;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + this.value;
	}
}
