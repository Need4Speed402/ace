package value;

import java.util.HashMap;

import parser.token.resolver.Unsafe;
import value.intrinsic.Assign;
import value.intrinsic.Compare;
import value.intrinsic.Function;
import value.intrinsic.Mutable;
import value.intrinsic.Print;
import value.node.Node;
import value.node.NodeIdentifier;

public class ValueDefaultEnv implements Value {
	private static ValueDefaultEnv instance = new ValueDefaultEnv();
	
	private ValueDefaultEnv () {
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
	public Value call(Value denv) {
		Value out = denv.getID(id -> {
			//System.out.println(id);
			return this.env.getOrDefault(id, denv);
		});
		
		return out;
	}
	
	public static void run (value.effect.Runtime runtime, Node root) {
		ValueProbe probe = new ValueProbe();
		//System.out.println(probe);
		Value gen = root.run(probe);
		//System.out.println("generated");
		System.out.println(gen);
		Value res = gen.resolve(probe, instance);
		//System.out.println("root resolved");
		System.out.println(res);
		runtime.run(res);
		
		//runtime.run(root.run(instance));
	}
}
