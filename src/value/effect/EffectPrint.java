package value.effect;

import value.Value;

public class EffectPrint implements Effect {
	private final String message;
	
	public EffectPrint (String message) {
		this.message = message;
	}
	
	@Override
	public void run(Runtime runtime, Value root) {
		runtime.out.println(this.message);
	}
	
	@Override
	public String toString() {
		return "Print(" + this.message + ")";
	}
}
