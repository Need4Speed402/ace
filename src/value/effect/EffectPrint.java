package value.effect;

public class EffectPrint implements Effect {
	private final String message;
	
	public EffectPrint (String message) {
		this.message = message;
	}
	
	@Override
	public void run(Runtime runtime) {
		runtime.out.println(this.message);
	}
	
	@Override
	public String toString() {
		return "Print(" + this.message + ")";
	}
}
