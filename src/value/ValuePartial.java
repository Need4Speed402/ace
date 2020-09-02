package value;

import parser.Color;
import parser.ProbeSet;
import value.resolver.ResolverMutable;
import value.resolver.Resolver;

public abstract class ValuePartial implements Value {
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
		public Value resolve(Resolver res) {
			return res.get(this);
		}

		@Override
		public void getResolves(ProbeSet set) {
			set.set(this);
		}
		
		@Override
		public String toString() {
			return "Probe(" + this.id + ")";
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
		public Value resolve(Resolver res) {
			Value a = this.parent.resolve(res);
			Value b = this.argument.resolve(res);
			
			if (a == this.parent & b == this.argument) {
				return this;
			}else {
				Value ret = a.call(b);
				
				if (res instanceof ResolverMutable) {
					ret = ret.resolve(res);
				}
				
				return ret;
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
		public Value resolve(Resolver res) {
			Value a = this.parent.resolve(res);
			Getter b = this.getter.resolve(res);
			
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
