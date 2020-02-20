package value.resolver;

import java.util.HashMap;

import value.Value;

public class ResolverUnsafe extends Resolver{
	private static HashMap<String, Value> unsafe = new HashMap<>();
	
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	public static final Value IDENTITY = v -> v;
	public static final Value SCOPE = v -> v.call(IDENTITY);
	
	static {
		unsafe.put("compare", v1 -> v2 -> v1.getName() == v2.getName() ? TRUE : FALSE);
		unsafe.put("assign", name -> value -> new Value () {
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
		});
		
		unsafe.put("root ``", a -> IDENTITY);
		unsafe.put("root Procedure", IDENTITY);
		unsafe.put("root Environment", IDENTITY);
		unsafe.put("root Package", SCOPE);
		unsafe.put("root Scope", SCOPE);
		unsafe.put("root Function", ident -> body -> arg -> body.call(env ->
			env.getName() == ident.getName()
				? arg
				: env
		));
		
		unsafe.put("Mutable", init -> new Mutable(init));
		
		unsafe.put("console", p -> {
			System.out.println(p);
			return IDENTITY;
		});
	}
	
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
	public Value exists(Resolver parent, String[] path) {
		return unsafe.get(String.join(" ", path));
	}
}
