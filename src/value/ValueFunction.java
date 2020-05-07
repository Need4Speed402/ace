package value;

import value.node.Node;
import value.ValueProbe.Resolve;

public class ValueFunction implements Value{
	private final Node gen;
	private final Resolve[] resolves;
	
	private ValueProbe probe = new ValueProbe();
	private Value root;
	
	public ValueFunction (Node gen) {
		this.gen = gen;
		this.resolves = new Resolve [] {};
	}
	
	private ValueFunction (Node gen, Resolve ... resolves) {
		this.gen = gen;
		this.resolves = resolves;
	}
	
	private Value getRoot () {
		if (this.root == null) {
			this.root = gen.run(this.probe);
			
			for (int i = 0; i < this.resolves.length; i++) {
				this.root = this.root.resolve(this.resolves[i].probe, this.resolves[i].value);
			}
		}
		
		return this.root;
	}
	
	public Value call (Value v) {
		return ValueEffect.wrap(v, this.getRoot().resolve(this.probe, ValueEffect.clear(v)));
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		Resolve[] nr = new Resolve[this.resolves.length + 1];
		System.arraycopy(this.resolves, 0, nr, 0, this.resolves.length);
		nr[nr.length - 1] = new Resolve(probe, value);
		
		return new ValueFunction(this.gen, nr);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString() + " -> " + this.probe.toString() + "\n|-");
		ValueProbe.append(this.getRoot().toString(), "  ", b);
		
		return b.toString();
	}
}
