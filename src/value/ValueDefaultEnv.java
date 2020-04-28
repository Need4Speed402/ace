package value;

import java.util.Arrays;
import java.util.HashMap;

import parser.token.resolver.Unsafe;
import value.effect.Effect;
import value.effect.EffectGet;
import value.effect.EffectPrint;
import value.effect.EffectSet;
import value.node.Node;
import value.node.NodeIdentifier;

public class ValueDefaultEnv implements Value {
	private static ValueDefaultEnv instance = new ValueDefaultEnv();
	
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	
	private ValueDefaultEnv () {
		this.put(Unsafe.COMPARE, v1 -> ValueEffect.wrap(v1, v2 -> {
			return ValueEffect.wrap(v2, v1.getID(v1id -> v2.getID(v2id -> {
				return v1id == v2id ? TRUE : FALSE;
			})));
		}));
		
		this.put(Unsafe.FUNCTION, ident -> ValueEffect.wrap(ident, ident.getID(identid -> body -> {
			return ValueEffect.wrap(body, new ValueFunction(probe -> body.call(penv -> penv.getID(envid -> identid == envid ? probe : penv))));
		})));
		
		this.put(Unsafe.ASSIGN, name -> ValueEffect.wrap(name, value -> ValueEffect.wrap(value, new Value () {
			@Override
			public Value call (Value v) {
				return value.call(v);
			}
			
			@Override
			public Value getID (Getter getter) {
				return name.getID(getter);
			}
			
			@Override
			public String toString() {
				return "Assignment(" + name.toString() + ") -> " + value.toString();
			}
		})));
		
		this.put(Unsafe.MUTABLE, init -> {
			Memory ret = new Memory();
			
			return new ValueEffect(v -> v
				.call(p -> new ValueEffect(p, p, new EffectSet(ret, init)))
				.call(p -> {
					ValueProbe u = new ValueProbe();
					return new ValueEffect(u, p, new EffectGet(ret, u));
				})
			, new EffectSet(ret, init));
		});
		
		this.put(Unsafe.CONSOLE, p -> {
			return p.getID(id -> {
				return new ValueEffect(p, p, new EffectPrint(NodeIdentifier.asString(id)));
			});
		});
	}
	
	private final HashMap<Integer, Value> env = new HashMap<>();
	
	private void put (NodeIdentifier ident, Value value) {
		env.put(ident.id, value);
	}

	@Override
	public Value call(Value denv) {
		Value out = denv.getID(id -> {
			return this.env.getOrDefault(id, denv);
		});
		
		return out;
	}
	
	public static void run (value.effect.Runtime runtime, Node root) {
		/*Value probe = new ValueProbe();
		System.out.println(probe);
		System.out.println(root.run(probe));*/
		
		Effect.runAll(runtime, root.run(instance));
	}
}
