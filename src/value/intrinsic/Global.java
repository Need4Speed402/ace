package value.intrinsic;

import parser.token.resolver.Unsafe;
import value.Value;
import value.ValueFunction;
import value.intrinsic.Compare.Pair;
import value.node.Node;

public class Global {
	public static final Value instance = new ValueFunction(env -> Compare.create(env, env,
		new Pair(Unsafe.COMPARE, Compare.instance),
		new Pair(Unsafe.FUNCTION, Function.instance),
		new Pair(Unsafe.ASSIGN, Assign.instance),
		new Pair(Unsafe.MUTABLE, Mutable.instance),
		new Pair(Unsafe.CONSOLE, Print.instance),
		new Pair(Unsafe.DPRINT, val -> {System.out.println(val); return val;})
	));
	
	public static Value exec (Node root) {
		return root.run(instance);
	}
}
