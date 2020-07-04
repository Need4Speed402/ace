package value;

import parser.Color;
import value.effect.Effect;
import value.effect.Runtime;
import value.intrinsic.Mutable;

public class ValueDefer extends ValuePartial{
	private final Probe probe;
	private final Value body, value;
	
	private ValueDefer(Probe probe, Value body, Value value) {
		this.probe = probe;
		this.body = body;
		this.value = value;
	}
	
	@Override
	public Value run(Runtime r) {
		Value arg = this.value.run(r);
		
		r.push();
		//r.declare(this.probe, arg);
		Value resolved = this.body.resolve(this.probe, arg).run(r);
		
		return r.pop(resolved);
	}
	
	public Value resolve (Probe probe, Value value) {
		return create(this.probe, this.body.resolve(probe, value), this.value.resolve(probe, value));
	}
	
	public static ValueEffect remapDeclares (ValueEffect ret) {
		for (Effect effect : ret.getEffects()) {
			if (effect instanceof Mutable.EffectDeclare) {
				ret = ret.resolve(((Mutable.EffectDeclare) effect).probe, new Probe());
			}
		}
		
		return ret;
	}
	
	public static Value create (Probe probe, Value body, Value val) {
		//return new ValueDefer(probe, body, val);
		
		if (val instanceof ValuePartial) {
			return new ValueDefer(probe, body, val);
		}else if (val instanceof ValueEffect) {
			return remapDeclares(new ValueEffect(
				create(probe, body, ((ValueEffect) val).getParent()),
				((ValueEffect) val).getEffects()
			));
		}else{
			Value v = body.resolve(probe, val);
			
			if (v instanceof ValueEffect) {
				v = remapDeclares((ValueEffect) v);
			}
			
			return v;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + " -> " + this.probe + "\n");
		b.append(Color.indent(this.body.toString(), "|-", "| "));
		b.append("\n");
		b.append(Color.indent(this.value.toString(), "|-", "  "));
		
		return b.toString();
	}
}
