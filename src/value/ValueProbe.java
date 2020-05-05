package value;

public class ValueProbe implements Value {
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		if (probe == this) {
			return value;
		}else {
			return this;
		}
	}
	
	public Value clear () {
		ValueProbe parent = this;
		
		return new ValueProbe () {
			@Override
			public Value resolve(ValueProbe probe, Value value) {
				Value v = parent.resolve(probe, value);
				
				return ValueEffect.clear(v);
			}
		};
	}
	
	@Override
	public Value call (Value arg) {
		return new Call(this, arg);
	}
	
	@Override
	public Value getID (Getter getter) {
		return new Identifier(this, getter);
	}
	
	public static void append(String s, String padding, StringBuilder b) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			b.append(c);
			
			if (c == '\n') {
				b.append(padding);
			}
		}
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
			b.append(super.toString() + "\n|-");
			append(this.parent.toString(), "| ", b);
			b.append("\n|-");
			append(this.argument.toString(), "  ", b);
			
			return b.toString();
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			Value ret = this.parent.resolve(probe, value).call(this.argument.resolve(probe, value));
			
			/*if (!(ret instanceof ValueProbe)) {
				ret = ret.resolve(probe, value);
			}*/
			
			return ret;
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
			b.append(super.toString() + "\n|-");
			append(this.parent.toString(), "  ", b);
			
			return b.toString();
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			Value ret = this.parent.resolve(probe, value).getID(this.getter);
			
			if (!(ret instanceof Identifier)) {
				ret = ret.resolve(probe, value);
			}
			
			return ret;
		}
	}
}
