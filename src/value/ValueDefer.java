package value;

import parser.Color;
import value.effect.Runtime;
import value.node.Node;

public class ValueDefer extends ValueProbe{
	private final ValueProbe probe;
	private final Value body, value;
	
	private ValueDefer(ValueProbe probe, Value body, Value value) {
		this.probe = probe;
		this.body = body;
		this.value = value;
	}
	
	@Override
	public Value run(Runtime r) {
		Value arg = this.value.run(r);
		
		return this.body.resolve(this.probe, arg).run(r);
	}
	
	public Value resolve (ValueProbe probe, Value value) {
		return eval(this.probe, this.body.resolve(probe, value), this.value.resolve(probe, value));
	}
	
	private static Value eval (ValueProbe probe, Value body, Value val) {
		if (val instanceof ValueProbe) {
			return new ValueDefer(probe, body, val);
		}else if (val instanceof ValueEffect) {
			Value p = ((ValueEffect) val).getParent();
			
			if (p instanceof ValueProbe) {
				body = new ValueDefer(probe, body, p);
			}else {
				body = body.resolve(probe, p);
			}
			
			return new ValueEffect(body, ((ValueEffect) val).getRawEffect());
		}else{
			return body.resolve(probe, val);
		}
	}
	
	public static Value accept (Node n) {
		return accept(n, null);
	}
	
	//this function exists just to fix a case with NodeIdentifier.NULL which will
	//cause the print rountine to enter an infinite loop and eventally stack
	//overflow. This methods lets you override the print routine.
	public static Value accept (Node n, String print) {
		return new DeferResolve(print, n, new ValueProbe());
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
	
	private static class DeferResolve implements Value {
		private final Generator gen;
		private final ValueProbe probe;
		private final String print;
		
		private Value cache;
		
		public DeferResolve (String print, Node node, ValueProbe probe) {
			this(print, () -> node.run(probe), probe);
		}
		
		public DeferResolve(String print, Generator gen, ValueProbe probe) {
			this.print = print;
			this.gen = gen;
			this.probe = probe;
		}
		
		private Value get () {
			if (this.cache == null) {
				this.cache = this.gen.generate();
			}
			
			return this.cache;
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			return new DeferResolve(this.print, () -> this.get().resolve(probe, value), this.probe);
		}
		
		@Override
		public Value call(Value v) {
			return eval(this.probe, this.get(), v);
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString()).append(" -> ").append(this.probe).append('\n');
			
			if (this.print != null) {
				b.append(Color.indent(this.print, "|-", "  ")); 
			}else {
				b.append(Color.indent(this.get().toString(), "|-", "  "));
			}
			
			return b.toString();
		}
		
		private static interface Generator {
			public Value generate ();
		}
	}
}
