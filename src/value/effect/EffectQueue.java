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
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + "\n");
		
		for (int i = 0; i < this.effects.length; i++) {
			Effect current = this.effects[i];
			
			b.append("\n");
			b.append(Color.indent(current.toString(), "|-", i + 1 >= this.effects.length ? "  " : "| "));
		}
		
		return b.toString();
	}
}
