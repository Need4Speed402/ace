package value.intrinsic;

import java.util.HashMap;

import parser.ProbeSet;
import parser.token.resolver.Unsafe;
import value.Value;
import value.ValuePartial.Probe;
import value.node.Node;
import value.node.NodeIdentifier;

public class Environment implements Value {
	private static Environment instance = new Environment();
	
	private Environment () {
		this.put(Unsafe.COMPARE, Compare.instance);
		this.put(Unsafe.FUNCTION, Function.instance);
		this.put(Unsafe.ASSIGN, Assign.instance);
		this.put(Unsafe.MUTABLE, Mutable.instance);
		this.put(Unsafe.CONSOLE, Print.instance);
	}
	
	private final HashMap<Integer, Value> env = new HashMap<>();
	
	private void put (NodeIdentifier ident, Value value) {
		env.put(ident.id, value);
	}

	@Override
	public Value call(Value env) {
		return env.getID(new IdentifierLookup(env));
	}
	
	public static Value exec (Node root) {
		return root.run(instance);
	}
	
	private class IdentifierLookup implements Getter {
		private final Value env;
		
		public IdentifierLookup(Value env) {
			this.env = env;
		}
		
		@Override
		public Value resolved(int value) {
			return Environment.this.env.getOrDefault(value, this.env);
		}
		
		@Override
		public Getter resolve(Probe probe, Value value) {
			Value r = this.env.resolve(probe, value);
			
			if (r == this.env) {
				return this;
			}else {
				return new IdentifierLookup(r);
			}
		}
		
		@Override
		public void getResolves(ProbeSet set) {
			this.env.getResolves(set);
		}
		
		@Override
		public String toString() {
			return super.toString() + " -> " + this.env;
		}
	}
}
