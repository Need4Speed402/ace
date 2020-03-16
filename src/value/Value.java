package value;

import java.util.HashMap;

import value.Delegate.Loader;

public interface Value {
	public Value call (Value environment);
	
	public default int getID () {
		return 0;
	}
	
	public static final HashMap<Call, Call> calls = new HashMap<>();
	public static final HashMap<Value, Environment> envs = new HashMap<>();
	
	public static final HashMap<String, Identifier> ids = new HashMap<>();
	public static final HashMap<Identifier, String> ids_rev = new HashMap<>();

	public static Call call (Value a, Value b) {
		Call c = new Call(a, b);
		Call mem = calls.get(c);
		
		if (mem == null) {
			calls.put(c, c);
			mem = c;
		}
		
		return mem;
	}
	
	public static Value delegate (Loader l) {
		return new Delegate(l);
	}
	
	public static Value env (Value a) {
		Environment mem = envs.get(a);
		
		if (mem == null) {
			mem = new Environment(a);
			envs.put(a, mem);
		}
		
		return mem;
	}
	
	public static Identifier id () {
		return new Identifier();
	}
	
	public static Value call (Value c, Value ... nodes) {
		for (int i = 0; i < nodes.length; i++) {
			c = Value.call(c, nodes[i]);
		}
		
		return c;
	}
	
	public static Identifier id (String ident) {
		Identifier mem = ids.get(ident);
		
		if (mem == null) {
			mem = Value.id();
			ids.put(ident, mem);
			ids_rev.put(mem, ident);
		}
		
		return mem;
	}
}
