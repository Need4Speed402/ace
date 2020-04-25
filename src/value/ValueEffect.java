package value;

import value.effect.Effect;

public class ValueEffect implements Value{
	private final Value parent;
	private final Effect[] effects;
	
	public ValueEffect(Value parent) {
		this.parent = unwrap(parent);
		this.effects = Value.NO_EFFECTS;
	}
	
	public ValueEffect(Value parent, Effect ... effects) {
		this.parent = unwrap(parent);
		this.effects = effects;
	}
	
	public ValueEffect (Value parent, Effect[] inherit, Effect ... effects) {
		this.parent = unwrap(parent);
		this.effects = Effect.join(inherit, effects);
	}
	
	public ValueEffect (Value parent, Effect[] ... effects) {
		this.parent = unwrap(parent);
		this.effects = Effect.join(effects);
	}
	
	private Value unwrap (Value p) {
		while (p instanceof ValueEffect) {
			p = ((ValueEffect) p).parent;
		}
		
		return p;
	}
	
	@Override
	public Value call(Value v) {
		Value c = this.parent.call(v);
		return new ValueEffect(c, this.getEffects(), c.getEffects());
	}
	
	@Override
	public Value getID(Getter getter) {
		Value c = this.parent.getID(getter);
		return new ValueEffect(c, this.getEffects(), c.getEffects());
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		Value c = this.parent.resolve(probe, value);
		
		return new ValueEffect(c, this.getEffects(), c.getEffects());
	}
	
	@Override
	public Effect[] getEffects() {
		return this.effects;
	}
	
	public Value getParent () {
		return this.parent;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + "\n|-");
		ValueProbe.append(this.parent.toString(), this.effects.length == 0 ? "  " : "| ", b);
		
		for (int i = 0; i < this.effects.length; i++) {
			b.append("\n|-");
			ValueProbe.append(this.effects[i].toString(), i + 1 == this.effects.length ? "  " : "| ", b);
		}
		
		return b.toString();
	}
	
	public static Value wrap (Value v, Value v2) {
		Effect[] effects = v.getEffects();
		
		if (effects.length == 0) {
			return v2;
		}else {
			return new ValueEffect(v2, effects, v2.getEffects());
		}
	}
	
	public static Value clear (Value v) {
		Effect[] effects = v.getEffects();
		
		if (effects.length == 0) {
			return v;
		}else {
			return new ValueEffect(v);
		}
	}
}
