package value;

import parser.Color;
import parser.ProbeSet;
import value.effect.Runtime;

public abstract class ValuePartial implements Value {
	@Override
	public abstract Value run(Runtime r);
	
	@Override
	public Value call (Value arg) {
		return new Call(this, arg);
	}
	
	@Override
	public Value getID (Getter getter) {
		return new Identifier(this, getter);
	}
	
	public static class Probe extends ValuePartial {
		private static int ids = 0;
		public final int id = ++ids;
		
		@Override
		public Value resolve(Probe probe, Value value) {
			return probe == this ? value : this;
		}

		@Override
		public Value run(Runtime r) {
			return r.get(this);
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			set.set(this);
		}
	}
	
	public static class Call extends ValuePartial {
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
		public Value run(Runtime r) {
			Value a = this.parent.run(r);
			Value b = this.argument.run(r);
			
			return a.call(b).run(r);
		}
		
		@Override
		public Value resolve(Probe probe, Value value) {
			Value a = this.parent.resolve(probe, value);
			Value b = this.argument.resolve(probe, value);
			
			if (a == this.parent & b == this.argument) {
				return this;
			}else {
				return a.call(b);
			}
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			this.parent.getResolves(set);
			this.argument.getResolves(set);
		}
	}
	
	public static class Identifier extends ValuePartial {
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
		public Value run(Runtime r) {
			Value p = this.parent.run(r);
			
			return p.getID(this.getter).run(r);
		}
		
		@Override
		public Value resolve(Probe probe, Value value) {
			Value a = this.parent.resolve(probe, value);
			Getter b = this.getter.resolve(probe, value);
			
			if (a == this.parent & b == this.getter) {
				return this;
			}else {
				return a.getID(b);
			}
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			this.parent.getResolves(set);
			this.getter.getResolves(set);
		}
	}
}
