package value;

import parser.Color;
import value.ValuePartial.Probe;
import value.node.Node;
import value.resolver.Resolver;
import value.resolver.ResolverFunctionBody;
import value.resolver.ResolverMutable;
import value.resolver.ResolverProbe;

public class ValueFunction implements Value {
	private final Generator body;
	private final Probe probe;
	
	private Value cache;
	
	public ValueFunction (Node node) {
		this.probe = new Probe();
		this.body = () -> node.run(this.probe);
	}
	
	private ValueFunction(Probe probe, Generator body) {
		this.body = body;
		this.probe = probe;
	}
	
	public Value get () {
		if (this.cache == null) {
			this.cache = this.body.generate();
		}
		
		return this.cache;
	}
	
	@Override
	public ValueFunction resolve(Resolver res) {
		// since we cannot guarantee the order the functions will be called,
		// functions will never be resolved if the resolver can mutate.
		if (res instanceof ResolverMutable) return this;
		
		if (res instanceof ResolverFunctionBody) {
			res = ((ResolverFunctionBody) res).lock();
			
			if (res == null) return this;
		}
		
		Resolver r = res;
		return new ValueFunction(
			this.probe,
			() -> this.get().resolve(r)
		);
	}
	
	@Override
	public int complexity() {
		return 1;
	}
	
	@Override
	public Value call(Value v) {
		if (v instanceof ValueEffect) {
			ValueEffect vv = (ValueEffect) v;
			return ValueEffect.create(this.call(vv.getParent()), vv.getEffectNode());
		}
		
		if ((v instanceof ValuePartial) && !(v instanceof Probe)) {
			return new ValuePartial.Call(this, v);
		}
		
		Value ret = this.get().resolve(new ResolverProbe(this.probe, v));
		//decide if the added complexity of resolving the probe
		//potentially early will pay off
		int deferComplexity = Value.add(v.complexity(), this.get().complexity());
		int substituteComplexity = ret.complexity();
		
		if (deferComplexity < substituteComplexity) {
			Value iret = this.get().resolve(new ResolverProbe(this.probe, this.probe.identify(v)));
			
			return new ValuePartial.Call(new ValueFunction(
				this.probe,
				() -> iret
			), v);
		}
		
		return ret.resolve(new ResolverFunctionBody());
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Function(").append(this.probe).append(")\n");
		b.append(Color.indent(this.get().toString(), "|-", "  "));
		
		return b.toString();
	}
	
	private static interface Generator {
		public Value generate ();
	}
}