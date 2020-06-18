package value;

import parser.Color;
import value.ValuePartial.Probe;
import value.node.Node;

public class ValueFunction implements Value {
		private final Generator gen;
		private final Probe probe;
		
		private Value cache;
		
		public ValueFunction (Node n) {
			this(n, new Probe());
		}
		
		private ValueFunction (Node node, Probe probe) {
			this(() -> node.run(probe), probe);
		}
		
		private ValueFunction(Generator gen, Probe probe) {
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
		public Value resolve(Resolver res) {
			return new ValueFunction(() -> this.get().resolve(res), this.probe);
		}
		
		@Override
		public Value call(Value v) {
			return ValueDefer.create(this.probe, this.get(), v);
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString()).append(" -> ").append(this.probe).append('\n');
			b.append(Color.indent(this.get().toString(), "|-", "  "));
			
			return b.toString();
		}
		
		private static interface Generator {
			public Value generate ();
		}
	}