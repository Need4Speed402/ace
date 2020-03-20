package value;

import parser.token.resolver.Unsafe;

import parser.Promise;
import value.node.Node;

public class ValueDefaultEnv implements Value {
	private ValueDefaultEnv () {}
	
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	
	public static final Value COMPARE = v1 -> v2 -> {
		ValueDelegate ret = new ValueDelegate();
		
		v1.getID().then(v1id -> {
			v2.getID().then(v2id -> {
				ret.resolve(v1id == v2id ? TRUE : FALSE);
			});
		});
		
		return ret;
	};
	
	public static final Value FUNCTION = ident -> body -> arg -> body.call(env -> {
		ValueDelegate ret = new ValueDelegate();
		
		ident.getID().then(identid -> {
			env.getID().then(envid -> {
				ret.resolve(identid == envid ? arg : env);
			});
		});
		
		return ret;
	});
	
	public static class Mutable implements Value {
		private Value value;
		
		public Mutable (Value value) {
			this.value = value;
		}
		
		@Override
		public Value call(Value v) {
			return v.call(p -> this.value = p).call(p -> this.value);
		}
	}

	@Override
	public Value call(Value denv) {
		ValueDelegate ret = new ValueDelegate();
		
		denv.getID().then(id -> {
			if (id == Unsafe.FUNCTION.id) {
				ret.resolve(FUNCTION);
			}else if (id == Unsafe.COMPARE.id) {
				ret.resolve(COMPARE);
			}else if (id == Unsafe.ASSIGN.id) {
				ret.resolve(name -> value -> new Value () {
					@Override
					public Value call (Value v) {
						return value.call(v);
					}
					
					@Override
					public Promise<Integer> getID () {
						return name.getID();
					}
					
					@Override
					public String toString() {
						return "Assignment(" + name.toString() + ") -> " + value.toString();
					}
				});
			}else if (id == Unsafe.MUTABLE.id) {
				ret.resolve(init -> new Mutable(init));
			}else if (id == Unsafe.CONSOLE.id) {
				ret.resolve(p -> {
					System.out.println(p);
					return p;
				});
			}else {
				ret.resolve(denv);
			}
		});
		
		return ret;
	}
	
	public static void run (Node root) {
		root.run(new ValueDefaultEnv());
	}
}
