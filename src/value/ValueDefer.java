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
		}else if (val instanceof ValueEffect){
			Value res = body.resolve(this.probe, ((ValueEffect) val).getParent());
			
			return new ValueEffect(res, val, res);
		}else {
			return body.resolve(this.probe, val);
		}
	}
	
	public static Value create (Value v, Node n) {
		if (v instanceof ValueProbe) {
			ValueProbe probe = new ValueProbe();
			Value body = n.run(probe);
			
			return new ValueDefer(probe, body, v);
		}else if (v instanceof ValueEffect){
			Value body = n.run(((ValueEffect) v).getParent());
			
			return new ValueEffect(body, v, body);
		}else{
			return n.run(v);
		}
	}
	
	public static Value accept (Node n) {
		return new DeferResolve(n);
	}
	
	private static class DeferResolve implements Value {
		private final Node node;
		
		public DeferResolve (Node node) {
			this.node = node;
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			return new DeferResolve(v -> this.node.run(v).resolve(probe, value));
		}
		
		@Override
		public Value call(Value v) {
			return create(v, this.node);
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
