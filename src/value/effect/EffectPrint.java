package value.effect;

import value.Value;

public class EffectPrint implements Effect {
	private final String message;
	
	public EffectPrint (String message) {
		this.message = message;
	}
	
	@Override
	public Value run(Runtime runtime, Value root) {
		runtime.out.println(this.message);
		return root;
	}
	
	@Override
	public String toString() {
		return "Print(" + this.message + ")";
	}
}
