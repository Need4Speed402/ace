package value.intrinsic;

import java.util.HashMap;

import parser.token.resolver.Unsafe;
import value.Value;
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
		return env.getID(new IdentifierLookup());
	}
	
	public static Value exec (Node root) {
		//Probe p = new Probe();
		//System.out.println(p);
		//System.out.println(root.run(p));
		
		return root.run(instance);
	}
	
	private class IdentifierLookup implements Getter {
		@Override
		public Value resolved(Value parent, int value) {
			return Environment.this.env.getOrDefault(value, parent);
		}
		
		@Override
		public int complexity() {
			return 1;
		}
		
		@Override
		public String toString() {
			return "DefaultEnvironmentLookup()";
		}
		
		@Override
		public String toString(Value ident) {
			return "DefaultEnvironmentLookup(" + ident + ")";
		}
	}
	
	@Override
	public String toString() {
		return "DefaultEnvironment";
	}
}
