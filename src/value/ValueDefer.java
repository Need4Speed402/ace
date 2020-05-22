package value;

import parser.Color;
import value.node.Node;

public class ValueDefer extends ValueProbe{
	private final ValueProbe probe;
	private final Value body, value;
	
	private ValueDefer(ValueProbe probe, Value body, Value value) {
		this.probe = probe;
		this.body = body;
		this.value = value;
	}
	
	public Value resolve (ValueProbe probe, Value value) {
		Value val = this.value.resolve(probe, value);
		Value body = this.body.resolve(probe, value);
		
		if (val instanceof ValueProbe) {
			return new ValueDefer(this.probe, body, val);
		}else if (val instanceof ValueEffect) {
			body = body.resolve(this.probe, ((ValueEffect) val).getParent());
			
			return new ValueEffect(body, ((ValueEffect) val).getEffects());
		}else{
			return body.resolve(this.probe, val);
		}
	}
	
	public static Value accept (Node n) {
		return new DeferResolve(n);
	}
	
	private static class DeferResolve implements Value {
		private final Node node;
		private final ValueProbe probe;
		
		private Value cache;
		
		public DeferResolve (Node node) {
			this(node, new ValueProbe());
		}
		
		public DeferResolve(Node node, ValueProbe probe) {
			this.node = node;
			this.probe = probe;
		}
		
		private Value get () {
			if (this.cache == null) {
				this.cache = this.node.run(this.probe);
			}
			
			return this.cache;
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			return new DeferResolve(v -> this.get().resolve(probe, value), this.probe);
		}
		
		@Override
		public Value call(Value v) {
			if (v instanceof ValueProbe) {
				return new ValueDefer(this.probe, this.get(), v);
			}else if (v instanceof ValueEffect){
				Value body = this.get().resolve(this.probe, ((ValueEffect) v).getParent());
				
				return new ValueEffect(body, ((ValueEffect) v).getEffects());
			}else{
				return this.get().resolve(this.probe, v);
			}
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString() + " -> " + this.probe + "\n");
			b.append(Color.indent(this.get().toString(), "|-", "  "));
			
			return b.toString();
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
