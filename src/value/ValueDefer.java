package value;

import parser.Color;
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
		
		return this.body.resolve(new ProbeResolver(this.probe, arg)).run(r);
	}
	
	public Value resolve (Resolver res) {
		return create(this.probe, this.body.resolve(res), this.value.resolve(res));
	}
	
	public static Value create (Probe probe, Value body, Value val) {
		if (val instanceof ValuePartial) {
			return new ValueDefer(probe, body, val);
		}else if (val instanceof ValueEffect) {
			val = val.resolve(new Mutable.Context());
			
			return new ValueEffect(
				create(probe, body, ((ValueEffect) val).getParent()),
				((ValueEffect) val).getRawEffect()
			);
		}else{
			return body.resolve(new ProbeResolver(probe, val));
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
