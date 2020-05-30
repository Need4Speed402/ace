package value;

import parser.Color;
import value.effect.Effect;
import value.effect.EffectPreCompute;
import value.effect.EffectProbe;
import value.effect.Runtime.Resolve;

public class ValueProbe implements Value {
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		if (probe == this) {
			return value;
		}else {
			return this;
		}
	}
	
	@Override
	public Value call (Value arg) {
		return new Call(this, arg);
	}
	
	@Override
	public Value getID (Getter getter) {
		return new Identifier(this, getter);
	}
	
	@Override
	public Effect getEffect () {
		return new EffectProbe(this);
	}
	
	public static class Call extends ValueProbe {
		public final Value parent;
		public final Value argument;
		
		public Call (Value parent, Value argument) {
			this.parent = parent;
			this.argument = argument;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString()).append('\n');
			b.append(Color.indent(this.parent.toString(), "|-", "| ")).append('\n');
			b.append(Color.indent(this.argument.toString(), "|-", "  "));
			
			return b.toString();
		}
		
		@Override
		public Effect getEffect() {
			ValueProbe a = new ValueProbe(), b = new ValueProbe();
			
			return new EffectPreCompute(
				new EffectProbe(a.call(b)),
				new Resolve(a, this.parent),
				new Resolve(b, this.argument)
			);
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			return this.parent.resolve(probe, value).call(this.argument.resolve(probe, value));
		}
	}
	
	public static class Identifier extends ValueProbe {
		public final Value parent;
		public final Getter getter;
		
		public Identifier (Value parent, Getter getter) {
			this.parent = parent;
			this.getter = getter;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString()).append('\n');
			b.append(Color.indent(this.parent.toString(), "|-", "| ")).append('\n');
			b.append(Color.indent(this.getter.toString(), "|-", "  "));
			
			return b.toString();
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			return this.parent.resolve(probe, value).getID(this.getter.resolve(probe, value));
		}
	}
}
