package value;

import parser.Color;
import value.resolver.Resolver;
import value.resolver.ResolverMutable;

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
		public final int id;
		
		private Probe (int id) {
			this.id = id;
		}
		
		public Probe () {
			this(++ids);
		}
		
		@Override
		public Value resolve(Resolver res) {
			return res.get(this);
		}

		@Override
		public int complexity() {
			return 1;
		}
		
		@Override
		public String toString() {
			return "Probe(" + this.id + ")";
		}
		
		public Probe identify (Value identifier) {
			return new ProbeKnownIdentifier(identifier);
		}
		
		private class ProbeKnownIdentifier extends Probe {
			private Value identifier;
			
			public ProbeKnownIdentifier (Value identifier) {
				super(Probe.this.id);
				this.identifier = identifier;
			}
			
			@Override
			public Value getID(Getter getter) {
				return this.identifier.getID(getter);
			}
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
			b.append("Call\n");
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
		public int complexity() {
			return Value.add(this.parent.complexity(), this.argument.complexity());
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
			String s = this.getter.toString(this.parent);
			
			if (s == null) {
				StringBuilder b = new StringBuilder();
				b.append("Identify\n");
				b.append(Color.indent(this.parent.toString(), "|-", "| ")).append('\n');
				b.append(Color.indent(this.getter.toString(), "|-", "  "));
				return b.toString();
			}else {
				return s;
			}
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
		public int complexity() {
			return Value.add(this.parent.complexity(), this.getter.complexity());
		}
		
		public Value caller (Value caller) {
			return new IdentifierKnownCall(this.parent, this.getter, caller);
		}
		
		private static class IdentifierKnownCall extends Identifier {
			private final Value caller;
			
			public IdentifierKnownCall(Value parent, Getter getter, Value caller) {
				super(parent, getter);
				
				this.caller = caller;
			}
			
			@Override
			public Value resolve(Resolver res) {
				Value r = super.resolve(res);
				
				if (r == this) {
					return this;
				}else if (r instanceof Identifier){
					return ((Identifier) r).caller(this.caller.resolve(res));
				}else {
					return r;
				}
			}
			
			@Override
			public Value call(Value arg) {
				return caller.call(arg);
			}
			
			@Override
			public Value getID(Getter getter) {
				return caller.getID(getter);
			}
		}
	}
}
