package value;

import value.resolver.Resolver;
import value.resolver.ResolverVirtual;
import value.resolver.ResolverVirtual.Pair;

public class Unsafe {
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	public static final Value IDENTITY = v -> v;
	public static final Value SCOPE = v -> v.call(IDENTITY);
	
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
			new Pair("compare", v1 -> v2 -> v1.getName() == v2.getName() ? TRUE : FALSE),
			new Pair("assign", name -> value -> new Value () {
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
			}),
			
			new Pair("Mutable", init -> new Mutable(init)),
			new Pair("console", p -> {
				System.out.println(p);
				return IDENTITY;
			}),
			
			new Pair("root", new ResolverVirtual(
				new Pair("``", a -> IDENTITY),
				new Pair("Procedure", IDENTITY),
				new Pair("Environment", IDENTITY),
				new Pair("Package", SCOPE),
				new Pair("Scope", SCOPE),
				new Pair("Function", ident -> body -> arg -> body.call(env ->
					env.getName() == ident.getName()
						? arg
						: env
				))
			))
		);
	}
}
