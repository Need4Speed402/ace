package value;

import java.util.Arrays;

import value.effect.Effect;
import value.node.Node;

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
	
	@Override
	public Effect[] getEffects() {
		return this.getRoot().getEffects();
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
	
	private static class Resolve {
		public final Value value;
		public final ValueProbe probe;
		
		public Resolve(ValueProbe probe, Value value) {
			this.probe = probe;
			this.value = value;
		}
	}
}
