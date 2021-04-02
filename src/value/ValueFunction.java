package value;

import value.ValuePartial.Probe;
import value.node.Node;
import value.resolver.Resolver;
import value.resolver.ResolverArgument;
import value.resolver.ResolverMutable;

public class ValueFunction implements Value{
	private final Generator body;
	private final Probe probe;

	private CallReturn cache;
	
	public ValueFunction (Node node) {
		this.probe = new Probe();
		this.body = () -> node.run(this.probe);
	}
	
	private ValueFunction(Probe probe, Generator body) {
		this.body = body;
		this.probe = probe;
	}
	
	public CallReturn get () {
		if (this.cache == null) {
			this.cache = this.body.generate();
		}
		
		return this.cache;
	}
	
	@Override
	public CallReturn call(Value v) {
		return this.get().resolve(new ResolverArgument(this.probe, v));
	}
	
	@Override
	public String toString() {
		return Value.print("Function " + this.probe, this.get());
	}
	
	private static interface Generator {
		public CallReturn generate ();
	}

	@Override
	public CallReturn resolve(Resolver res) {
		// since we cannot guarantee the order the functions will be called,
		// functions will never be resolved if the resolver can mutate.
		if (res instanceof ResolverMutable) return new CallReturn(this);
		if (res instanceof ResolverArgument) res = ((ResolverArgument) res).add(this.probe);
		
		// this alias is here to get around java's dumb mutation rules around closures.
		Resolver r = res;
		return new CallReturn (new ValueFunction(
			(Probe) res.resolveValue(this.probe),
			() -> this.get().resolve(r)
		));
	}
}