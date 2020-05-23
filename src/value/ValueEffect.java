package value;

import parser.Color;
import value.effect.Effect;
import value.effect.EffectQueue;

public class ValueEffect implements Value{
	private final Value parent;
	private final Effect effect;
	
	public ValueEffect(Value parent, Effect effect) {
		if (parent instanceof ValueEffect) {
			effect = new EffectQueue(effect, ((ValueEffect) parent).getEffect());
			parent = ((ValueEffect) parent).getParent();
		}
		
		this.parent = parent;
		this.effect = effect;
	}
	
	@Override
	public Value call(Value v) {
		Value c = this.parent.call(v);
		
		return new ValueEffect(c, this.effect);
	}
	
	@Override
	public Value getID(Getter getter) {
		Value c = this.parent.getID(getter);
		
		return new ValueEffect(c, this.effect);
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		return new ValueEffect(this.parent.resolve(probe, value), this.effect.resolve(probe, value));
	}
	
	public Effect getEffect () {
		return this.effect;
	}
	
	public Value getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + "\n");
		b.append(Color.indent(this.effect.toString(), "|-", "  "));
		return b.toString();
	}
}
