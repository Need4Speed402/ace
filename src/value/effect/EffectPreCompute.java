package value.effect;

import value.Value;
import value.ValueProbe;
import value.effect.Runtime.Resolve;

public class EffectPreCompute implements Effect {
	private final Resolve[] mappings;
	private final Effect body;
	
	public EffectPreCompute (Effect body, Resolve ... mappings) {
		this.body = body;
		this.mappings = mappings;
	}
	
	@Override
	public void run (Runtime runtime) {
		Resolve[] noe = new Resolve[this.mappings.length];
		
		for (int i = 0; i < noe.length; i++) {
			noe[i] = mappings[i].runEffects(runtime);
		}
		
		runtime = new Runtime(runtime);
		
		for (int i = 0; i < noe.length; i++) {
			runtime.setResolve(noe[i]);
		}
		
		this.body.run(runtime);
	}
	
	@Override
	public Effect resolve(ValueProbe probe, Value value) {
		Resolve[] nm = new Resolve[this.mappings.length];
		
		for (int i = 0; i < this.mappings.length; i++) {
			nm[i] = new Resolve(this.mappings[i].probe, this.mappings[i].value.resolve(probe, value));
		}
		
		return new EffectPreCompute(this.body.resolve(probe, value), nm);
	}
}
