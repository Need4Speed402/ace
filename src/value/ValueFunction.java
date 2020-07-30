package value;

import parser.Color;
import parser.Packages;
import parser.ProbeSet;
import value.ValuePartial.Probe;
import value.effect.Effect;
import value.effect.Runtime;
import value.intrinsic.Mutable;
import value.node.Node;

public class ValueFunction implements Value {
	private final Generator gen;
	private final Probe probe;
	private final ProbeSet probes;
	
	private Value cache;
	
	public ValueFunction (Node n, ProbeSet.Resolver ... probes) {
		this(n, new Probe(), probes);
	}
	
	private ValueFunction (Node node, Probe probe, ProbeSet.Resolver ... probes) {
		this(() -> node.run(probe), new ProbeSet(probes), probe);
	}
	
	private ValueFunction(Generator gen, ProbeSet probes, Probe probe) {
		this.gen = gen;
		this.probes = probes;
		this.probe = probe;
	}
	
	private Value get () {
		if (this.cache == null) {
			this.cache = this.gen.generate();
		}
		
		return this.cache;
	}
	
	@Override
	public ValueFunction resolve(Probe probe, Value value) {
		if (this.probes.has(probe)) {
			return new ValueFunction(
				() -> this.get().resolve(probe, value),
				this.probes.replace(probe, value),
				this.probe
			);
		}else {
			return this;
		}
	}
	
	@Override
	public void getResolves(ProbeSet set) {
		set.set(this.probes);
	}
	
	private static ValueEffect remapDeclares (ValueEffect ret) {
		for (Effect effect : ret.getEffects()) {
			if (effect instanceof Mutable.EffectDeclare) {
				ret = ret.resolve(((Mutable.EffectDeclare) effect).probe, new Probe());
			}
		}
		
		return ret;
	}
	
	@Override
	public Value call(Value v) {
		// to effectively disable the optimizer, this code can be uncommented
		// which will basically make all functions be evaluated at runtime
		// when most can be evaluated at compile time.
		//if (true) return new ValuePartial.Call(this, v);
		
		if (v instanceof ValuePartial) {
			return new ValuePartial.Call(this, v);
		}else if (v instanceof ValueEffect) {
			if (((ValueEffect) v).getParent() instanceof ValuePartial) {
				return new ValuePartial.Call(this, v);
			}
			
			return remapDeclares(new ValueEffect(
				this.get().resolve(probe, ((ValueEffect) v).getParent()),
				((ValueEffect) v).getEffects()
			));
		}else{
			Value ret = this.get().resolve(probe, v);
			
			if (ret instanceof ValueEffect) {
				ret = remapDeclares((ValueEffect) ret);
			}
			
			return ret;
		}
	}
	
	@Override
	public Value run(Runtime r) {
		return new RuntimeFunction(r, this);
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
	
	private static class RuntimeFunction implements Value{
		private final Runtime runtime;
		private final ValueFunction func;
		
		public RuntimeFunction (Runtime runtime, ValueFunction func) {
			this.runtime = runtime;
			this.func = func;
		}
		
		@Override
		public RuntimeFunction resolve(Probe probe, Value value) {
			return new RuntimeFunction(this.runtime, this.func.resolve(probe, value));
		}
		
		@Override
		public Value call(Value v) {
			if (v instanceof ValuePartial) throw new Error("what");
			
			Runtime child = this.runtime.push();
			child.declare(this.func.probe, v);
			
			long start = System.nanoTime();
			Value ret = this.func.get();
			Packages.RESOLVE_TIME += System.nanoTime() - start;
			
			ret = ret.run(child);
			
			return this.runtime.root.extend(child, ret);
		}
	}
}