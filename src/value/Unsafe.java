package value;

import resolver.Resolver;
import resolver.ResolverVirtual;
import resolver.ResolverVirtual.Pair;
import value.node.Node;

public class Unsafe {
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	
	public static final Node IDENTITY = Node.call("(function)", Node.id("{identity}"), Node.env(Node.id("{identity}")));
	public static final Node SCOPE = Node.call("(function)", Node.id("{scope}"), Node.env(Node.call(Node.id("{scope}"), IDENTITY)));
	public static final Node DO = Node.call("(function)", Node.id("void"), Node.env(IDENTITY));
	
	public static final Value DEFAULT_ENVIRONMENT = denv -> {
		if (denv.getName() == "(function)") {
			return ident -> body -> arg -> body.call(env ->
				env.getName() == ident.getName()
					? arg
					: env
			);
		}else if (denv.getName() == "(compare)") {
			return v1 -> v2 -> v1.getName() == v2.getName() ? TRUE : FALSE;
		}else if (denv.getName() == "(assign)") {
			return name -> value -> new Value () {
				@Override
				public Value call (Value v) {
					return value.call(v);
				}
				
				@Override
				public String getName () {
					return name.getName();
				}
				
				@Override
				public String toString() {
					return "Assignment(" + name.getName() + ") -> " + value.toString();
				}
			};
		}else if (denv.getName() == "(mutable)") {
			return init -> new Mutable(init);
		}else if (denv.getName() == "console") {
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
			new Pair("compare", Node.id("(copmare)")),
			new Pair("assign", Node.id("(assign)")),
			new Pair("Mutable", Node.id("(mutable)")),
			new Pair("console", Node.id("(console)")),
			
			new Pair("root", new ResolverVirtual(
				new Pair("``", Node.call("(function)", Node.id(""), IDENTITY)),
				new Pair("Procedure", IDENTITY),
				new Pair("Environment", IDENTITY),
				new Pair("Package", SCOPE),
				new Pair("Scope", SCOPE),
				new Pair("Function", Node.id("(function)"))
			))
		);
	}
}
