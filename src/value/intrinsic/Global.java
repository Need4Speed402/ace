package value.intrinsic;

import parser.token.resolver.Unsafe;
import runtime.Effect;
import value.Value;
import value.Value.CallReturn;
import value.ValueFunction;
import value.node.Node;

public class Global {
	public static final Value instance = new ValueFunction(env -> new CallReturn(create(env,
		new Pair(Unsafe.COMPARE, Compare.instance),
		new Pair(Unsafe.FUNCTION, Function.instance),
		new Pair(Unsafe.ASSIGN, Assign.instance),
		new Pair(Unsafe.MUTABLE, Mutable.instance),
		new Pair(Unsafe.CONSOLE, Print.instance),
		new Pair(Unsafe.DPRINT, val -> {System.out.println(val); return new CallReturn(val);})
	)));
	
	public static Effect exec (Node root) {
		return root.run(instance).effect;
	}
	
	private static class Pair {
		public final Value identifier, instance;
		
		public Pair (Value identifier, Value instance) {
			this.identifier = identifier;
			this.instance = instance;
		}
	}
	
	private static Value create (Value def, Pair ... pairs) {
		Value current = def;
		
		for (int i = 0; i < pairs.length; i++) {
			current = Compare.create(def, current, pairs[i].identifier, pairs[i].instance);
		}
		
		return current;
	}
}
