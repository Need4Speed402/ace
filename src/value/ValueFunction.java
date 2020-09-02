package value;

import parser.Color;
import parser.ProbeSet;
import value.ValuePartial.Probe;
import value.node.Node;
import value.resolver.Resolver;
import value.resolver.ResolverFunctionBody;
import value.resolver.ResolverMutable;
import value.resolver.ResolverProbe;

public class ValueFunction implements Value {
	private final Generator body;
	private final Probe probe;
	
	private ProbeSet probeCache, cleanProbeCache;
	private Value cache;
	
	public ValueFunction (Node node) {
		this.probe = new Probe();
		this.body = () -> node.run(this.probe);
	}
	
	public ValueFunction (Node node, ProbeSet.ProbeContainer ... resolvers) {
		this.probe = new Probe();
		this.body = () -> node.run(this.probe);
		this.probeCache = new ProbeSet(resolvers);
	}
	
	private ValueFunction(Generator body, ProbeSet probes, Probe probe) {
		this.body = body;
		this.probeCache = probes;
		this.probe = probe;
	}
	
	public Value get () {
		if (this.cache == null) {
			this.cache = this.body.generate();
		}
		
		return this.cache;
	}
	
	private ProbeSet getProbes () {
		if (this.probeCache == null) {
			this.probeCache = new ProbeSet(this.get());
		}
		
		return this.probeCache;
	}
	
	@Override
	public ValueFunction resolve(Resolver res) {
		// since we cannot guarantee the order the functions will be called,
		// functions will never be resolved if the resolver can mutate.
		if (res instanceof ResolverMutable) return this;
		
		ProbeSet probes = this.getProbes();
		
		if (res instanceof ResolverFunctionBody) {
			res = ((ResolverFunctionBody) res).lock();
			
			if (res == null) return this;
		}
		
		if (res.has(probes)) {
			Resolver fres = res;
			
			return new ValueFunction(
				() -> this.get().resolve(fres),
				null, //probes.resolve(probe, value),
				this.probe
			);
		}else{
			return this;
		}
	}
	
	@Override
	public void getResolves(ProbeSet set) {
		if (this.cleanProbeCache == null) {
			this.cleanProbeCache = this.getProbes().remove(this.probe);
		}
		
		set.set(this.cleanProbeCache);
	}
	
	private Value getResolved (Value v) {
		if (this.getProbes().has(this.probe)) {
			return this.get().resolve(new ResolverProbe(this.probe, v));
		}else {
			return this.get();
		}
	}
	
	@Override
	public Value call(Value v) {
		if (v instanceof ValuePartial && !(v instanceof Probe)) {
			return new ValuePartial.Call(this, v);
		}else{
			Value ret;
			
			if (v instanceof ValueEffect) {
				ValueEffect vv = (ValueEffect) v;
				
				if (vv.getParent() instanceof ValuePartial) {
					//return ValueEffect.create(new ValuePartial.Call(this, vv), vv.getEffectNode());
					return new ValuePartial.Call(this, v);
				}
				
				ret = ValueEffect.create(
					this.getResolved(vv.getParent()),
					vv.getEffectNode()
				);
			}else {
				ret = this.getResolved(v);
			}
			
			return ret.resolve(new ResolverFunctionBody());
		}
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