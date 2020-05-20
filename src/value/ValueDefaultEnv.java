package value;

import java.util.HashMap;

import parser.token.resolver.Unsafe;
import value.effect.EffectPrint;
import value.effect.EffectSet;
import value.node.Node;
import value.node.NodeIdentifier;

public class ValueDefaultEnv implements Value {
	private static ValueDefaultEnv instance = new ValueDefaultEnv();
	
	public static final Value TRUE = ValueDefer.accept(p1 -> ValueDefer.accept(p2 -> p1));
	public static final Value FALSE = ValueDefer.accept(p1 -> ValueDefer.accept(p2 -> p2));
	
	private ValueDefaultEnv () {
		this.put(Unsafe.COMPARE, ValueDefer.accept(v1 -> ValueDefer.accept(v2 ->
			v1.getID(v1id -> v2.getID(v2id -> v1id == v2id ? TRUE : FALSE))
		)));
		
		this.put(Unsafe.FUNCTION, ident -> ident.getID(identid -> 
			ValueDefer.accept(body -> new ValueFunction(probe -> body.call(penv -> penv.getID(envid -> identid == envid ? probe : penv))))
		));
		
		this.put(Unsafe.ASSIGN, ValueDefer.accept(name -> ValueDefer.accept(value -> new ValueAssign(name, value))));
		
		this.put(Unsafe.MUTABLE, ValueDefer.accept(init -> {
			ValueProbe probe = new ValueProbe();
			
			Value setup = v -> v
				.call(p -> new ValueEffect(p, p, new EffectSet(probe, p)))
				.call(p -> new ValueEffect(probe, probe, p));
			
			return new ValueEffect(setup, setup, init, new EffectSet(probe, init));
		}));
		
		this.put(Unsafe.CONSOLE, ValueDefer.accept(p -> 
			p.getID(id -> {
				return new ValueEffect(p, new EffectPrint(NodeIdentifier.asString(id)));
			}))
		);
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
		//System.out.println(gen);
		Value res = gen.resolve(probe, instance);
		//System.out.println("root resolved");
		//System.out.println(res);
		runtime.run(res);
		
		//runtime.run(root.run(instance));
	}
}
