package value;

import java.util.Arrays;

import value.effect.Effect;

public class ValueEffect implements Value{
	private final Value parent;
	private final Value[] effects;
	
	public ValueEffect(Value parent) {
		this(parent, new Value[] {});
	}
	
	public ValueEffect(Value parent, Effect ... effects) {
		this(parent, new EffectWrapper(effects));
	}
	
	public ValueEffect(Value parent, Value inherit, Effect effect) {
		this(parent, inherit, new EffectWrapper(new Effect[] {effect}));
	}
	
	public ValueEffect (Value parent, Value ... effects) {
		this.parent = unwrap(parent);
		this.effects = effects;
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
		return new ValueEffect(c, this, c);
	}
	
	@Override
	public Value getID(Getter getter) {
		Value c = this.parent.getID(getter);
		return c;//new ValueEffect(c, this, c);
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		Value c = this.parent.resolve(probe, value);
		Value[] resolved = new Value[this.effects.length];
		
		for (int i = 0; i < this.effects.length; i++) {
			if (this.effects[i] == this.parent) {
				resolved[i] = c;
			}else {
				resolved[i] = this.effects[i].resolve(probe, value);
			}
		}
		
		return new ValueEffect(c, resolved);
	}
	
	@Override
	public Effect[] getEffects() {
		Effect[][] effects = new Effect[this.effects.length][];
		
		for (int i = 0; i < this.effects.length; i++) {
			effects[i] = this.effects[i].getEffects();
		}
		
		return Effect.join(effects);
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
		return new ValueEffect(v2, v, v2);
	}
	
	public static Value clear (Value v) {
		return new ValueEffect(v);
	}
	
	private static class EffectWrapper implements Value {
		private final Effect[] effects;
		
		public EffectWrapper(Effect[] effects) {
			this.effects = effects;
		}
		
		@Override
		public Effect[] getEffects() {
			return this.effects;
		}
		
		@Override
		public Value call(Value v) {
			throw new RuntimeException("EffectWrapper is only used for its effects, this cannot be called");
		}
		
		@Override
		public String toString() {
			return super.toString() + " " + Arrays.toString(this.effects);
		}
	}
}
