package value;

import parser.Color;
import value.node.Node;

public class ValueFunction implements Value{
	private final Generator gen;
	private final ValueProbe probe;
	
	private Value root;
	
	public ValueFunction (Node gen) {
		this.probe = new ValueProbe();
		this.gen = () -> gen.run(this.probe);
	}
	
	private ValueFunction (Generator gen, ValueProbe probe) {
		this.gen = gen;
		this.probe = probe;
	}
	
	private Value getRoot () {
		if (this.root == null) {
			this.root = gen.generate();
		}
		
		return this.root;
	}
	
	public Value call (Value v) {
		return ValueEffect.wrap(v, this.getRoot().resolve(this.probe, ValueEffect.clear(v)));
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		return new ValueFunction(() -> this.getRoot().resolve(probe, value), this.probe);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + " -> " + this.probe.toString() + "\n");
		b.append(Color.indent(this.getRoot().toString(), "|-", "  "));
		
		return b.toString();
	}
	
	private interface Generator {
		public Value generate();
	}
}
