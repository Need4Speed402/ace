package value;

import parser.Color;
import value.effect.Effect;

public class ValueEffect implements Value{
	private final Value parent;
	private final Effect[] effects;
	
	public ValueEffect(Value parent, Effect ... effects) {
		if (parent instanceof ValueEffect) {
			Effect[] ie = ((ValueEffect) parent).effects;
			Effect[] ne = new Effect[effects.length + ie.length];
			System.arraycopy(effects, 0, ne, 0, effects.length);
			System.arraycopy(ie, 0, ne, effects.length, ie.length);
			
			effects = ne;
			parent = ((ValueEffect) parent).parent;
		}
		
		this.parent = parent;
		this.effects = effects;
	}
	
	@Override
	public Value call(Value v) {
		Value c = this.parent.call(v);
		
		return new ValueEffect(c, this.effects);
	}
	
	@Override
	public Value getID(Getter getter) {
		Value c = this.parent.getID(getter);
		
		return new ValueEffect(c, this.effects);
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		Effect[] ne = new Effect[this.effects.length];
		
		for (int i = 0; i < this.effects.length; i++) {
			ne[i] = this.effects[i].resolve(probe, value);
		}
		
		return new ValueEffect(this.parent.resolve(probe, value), ne);
	}
	
	public Effect[] getEffects () {
		return this.effects;
	}
	
	public Value getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + "\n");
		b.append(Color.indent(this.parent.toString(), "|-", this.effects == null ? "  " : "| "));
		
		for (int i = 0; i < this.effects.length; i++) {
			Effect current = this.effects[i];
			
			b.append("\n");
			b.append(Color.indent(current.toString(), "|-", i + 1 >= this.effects.length ? "  " : "| "));
		}
		
		return b.toString();
	}
}
