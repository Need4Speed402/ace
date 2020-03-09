package value;

import resolver.Resolver;
import resolver.ResolverVirtual;
import resolver.ResolverVirtual.Pair;
import value.node.Node;
import value.node.NodeIdentifier;

public class Unsafe {
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	
	public static final NodeIdentifier FUNCTION = Node.id();
	public static final NodeIdentifier COMPARE = Node.id();
	public static final NodeIdentifier ASSIGN = Node.id();
	public static final NodeIdentifier MUTABLE = Node.id();
	public static final NodeIdentifier CONSOLE = Node.id();
	
	private static final Node TEMP1 = Node.id();
	private static final Node TEMP2 = Node.id();
	
	public static final Node IDENTITY = Node.call(FUNCTION, TEMP1, Node.env(TEMP1));
	public static final Node SCOPE = Node.call(FUNCTION, TEMP2, Node.env(Node.call(TEMP2, IDENTITY)));
	public static final Node DO = Node.call(FUNCTION, Node.id(), Node.env(IDENTITY));
	
	public static final Value DEFAULT_ENVIRONMENT = denv -> {
		if (denv.getID() == FUNCTION.id) {
			return ident -> body -> arg -> body.call(env ->
				env.getID() == ident.getID()
					? arg
					: env
			);
		}else if (denv.getID() == COMPARE.id) {
			return v1 -> v2 -> v1.getID() == v2.getID() ? TRUE : FALSE;
		}else if (denv.getID() == ASSIGN.id) {
			return name -> value -> new Value () {
				@Override
				public Value call (Value v) {
					return value.call(v);
				}
				
				@Override
				public int getID () {
					return name.getID();
				}
				
				@Override
				public String toString() {
					return "Assignment(" + super.toString() + ") -> " + value.toString();
				}
			};
		}else if (denv.getID() == MUTABLE.id) {
			return init -> new Mutable(init);
		}else if (denv.getID() == CONSOLE.id) {
			return p -> {
				System.out.println(p);
				return p;
			};
		}
		
		return denv;
	};
	
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
	
	public static Resolver createUnsafe () {
		return new ResolverVirtual(
			new Pair("compare", COMPARE),
			new Pair("assign", ASSIGN),
			new Pair("Mutable", MUTABLE),
			new Pair("console", CONSOLE),
			
			new Pair("root", new ResolverVirtual(
				new Pair("``", DO),
				new Pair("Procedure", IDENTITY),
				new Pair("Environment", IDENTITY),
				new Pair("Package", SCOPE),
				new Pair("Scope", SCOPE),
				new Pair("Function", FUNCTION)
			))
		);
	}
}
