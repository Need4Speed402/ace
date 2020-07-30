package value;

import parser.Color;
import parser.ProbeSet;
import value.ValuePartial.Probe;
import value.effect.Effect;
import value.effect.Runtime;

public class ValueEffect implements Value{
	private final Value parent;
	private final Effect[] effects;
	
	public ValueEffect(Value parent, Effect ... effects) {
		if (parent instanceof ValueEffect) {
			Effect[] peffects = ((ValueEffect) parent).getEffects();
			
			Effect[] ne = new Effect[effects.length + peffects.length];
			System.arraycopy(effects, 0, ne, 0, effects.length);
			System.arraycopy(peffects, 0, ne, effects.length, peffects.length);
			
			effects = ne;
			parent = ((ValueEffect) parent).getParent();
		}
		
		this.parent = parent;
		this.effects = effects;
	}
	
	@Override
	public Value call(Value v) {
		return new ValueEffect(this.parent.call(v), this.effects);
	}
	
	@Override
	public Value getID(Getter getter) {
		return new ValueEffect(this.parent.getID(getter), this.effects);
	}
	
	@Override
	public ValueEffect resolve(Probe probe, Value value) {
		Effect[] ne = new Effect[this.effects.length];
		boolean equal = true;
		
		for (int i = 0; i < ne.length; i++) {
			ne[i] = this.effects[i].resolve(probe, value);
			
			if (ne[i] != this.effects[i]) {
				equal = false;
			}
		}
		
		Value pr = this.parent.resolve(probe, value);
		
		if (pr == this.parent && equal) {
			return this;
		}else if (equal) {
			return new ValueEffect(pr, this.effects);
		}else {
			return new ValueEffect(pr, ne);
		}
	}
	
	@Override
	public void getResolves(ProbeSet set) {
		this.parent.getResolves(set);
		
		for (int i = 0; i < this.effects.length; i++) {
			this.effects[i].getResolves(set);
		}
	}
	
	public Effect[] getEffects () {
		return this.effects;
	}
	
	@Override
	public Value run (Runtime r) {
		for (int i = 0; i < this.effects.length; i++) {
			this.effects[i].run(r);
		}
		
		return this.parent.run(r);
	}
	
	public Value getParent() {
		return this.parent;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString()).append('\n');
		b.append(Color.indent(this.parent.toString(), "|-", "| ")).append('\n');
		
		for (int i = 0; i < this.effects.length; i++) {
			b.append(Color.indent(this.effects[i].toString(), "|-", i + 1 == this.effects.length ? "  " : "| "));
			
			if (i + 1 < this.effects.length) {
				b.append('\n');
			}
		}
		
		return b.toString();
	}
}
