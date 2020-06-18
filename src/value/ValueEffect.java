package value;

import parser.Color;
import value.effect.Effect;
import value.effect.EffectQueue;
import value.effect.Runtime;

public class ValueEffect implements Value{
	private final Value parent;
	private final Effect effect;
	
	public ValueEffect(Value parent, Effect effect) {
		if (parent instanceof ValueEffect) {
			Effect peffect = ((ValueEffect) parent).getRawEffect();
			
			if (peffect instanceof EffectQueue) {
				effect = new EffectQueue(effect).concat((EffectQueue) peffect);
			}else {
				effect = new EffectQueue(effect, peffect);
			}
			
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
	public Value resolve(Resolver res) {
		return new ValueEffect(this.parent.resolve(res), this.effect.resolve(res));
	}
	
	public Effect getRawEffect () {
		return this.effect;
	}
	
	@Override
	public Value run (Runtime r) {
		this.effect.run(r);
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
		b.append(Color.indent(this.effect.toString(), "|-", "  "));
		return b.toString();
	}
}
