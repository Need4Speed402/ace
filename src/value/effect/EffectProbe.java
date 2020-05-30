package value.effect;


import value.Value;
import value.ValueProbe;

public class EffectProbe implements Effect{
	private final Value value;
	
	public EffectProbe (Value value) {
		this.value = value;
	}
	
	@Override
	public void run(Runtime runtime) {
		Value val = runtime.apply(this.value);
		
		if (val instanceof ValueProbe) {
			System.out.println(runtime);
			throw new RuntimeException("BUG: Runtime and compile information not enough to satisfy: " + val.toString());
		}
		
		val.getEffect().run(runtime);
	}

	@Override
	public Effect resolve(ValueProbe probe, Value value) {
		return new EffectProbe(this.value.resolve(probe, value));
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + this.value.toString();
	}
}
