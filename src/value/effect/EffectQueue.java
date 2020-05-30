package value.effect;

import parser.Color;
import value.Value;
import value.ValueProbe;

public class EffectQueue implements Effect {
	private final Effect[] effects;
	
	public EffectQueue (Effect ... effects) {
		this.effects = effects;
	}
	
	@Override
	public Effect resolve(ValueProbe probe, Value value) {
		Effect[] ne = new Effect[this.effects.length];
		
		for (int i = 0; i < this.effects.length; i++) {
			ne[i] = this.effects[i].resolve(probe, value);
		}
		
		return new EffectQueue(ne);
	}
	
	@Override
	public void run(Runtime runtime) {
		for (int i = 0; i < this.effects.length; i++) {
			this.effects[i].run(runtime);
		}
	}
	
	public EffectQueue concat (EffectQueue queue) {
		Effect[] effects = queue.effects;
		
		Effect[] ne = new Effect[this.effects.length + effects.length];
		System.arraycopy(this.effects, 0, ne, 0, this.effects.length);
		System.arraycopy(effects, 0, ne, this.effects.length, effects.length);
		
		return new EffectQueue(ne);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString());
		
		for (int i = 0; i < this.effects.length; i++) {
			Effect current = this.effects[i];
			
			b.append("\n");
			b.append(Color.indent(current.toString(), "|-", i + 1 >= this.effects.length ? "  " : "| "));
		}
		
		return b.toString();
	}
}
