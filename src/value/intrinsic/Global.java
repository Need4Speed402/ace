package value.intrinsic;

import parser.token.resolver.Unsafe;
import runtime.Effect;
import value.Value;
import value.Value.CallReturn;
import value.ValueFunction;
import value.intrinsic.Compare.Pair;
import value.node.Node;

public class Global {
	public static final Value instance = new ValueFunction(env -> new CallReturn(Compare.create(env,
		new Pair(Unsafe.COMPARE, Compare.instance),
		new Pair(Unsafe.FUNCTION, Function.instance),
		new Pair(Unsafe.ASSIGN, Assign.instance),
		new Pair(Unsafe.MUTABLE, Mutable.instance),
		new Pair(Unsafe.CONSOLE, Print.instance),
		new Pair(Unsafe.DPRINT, val -> {System.out.println(val); return new CallReturn(val);})
	)));
	
	public static Effect exec (Node root) {
		CallReturn program = root.run(instance);
		return program.effect;
	}
}
