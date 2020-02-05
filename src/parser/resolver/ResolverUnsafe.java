package parser.resolver;

import java.util.HashMap;

import value.Value;
import value.ValueIdentifier;

public class ResolverUnsafe extends Resolver{
	private static HashMap<String, Value> unsafe = new HashMap<>();
	
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	public static final Value IDENTITY = v -> v;
	public static final Value RESOLVE = v -> v instanceof ValueIdentifier ? ((ValueIdentifier) v).value : v;
	public static final Value SCOPE = v -> v.call(RESOLVE);
	
	public static boolean compare (Value v1, Value v2) {
		return
			v1 instanceof ValueIdentifier &&
			v2 instanceof ValueIdentifier &&
			((ValueIdentifier) v1).name == ((ValueIdentifier) v2).name;
	}
	
	static {
		unsafe.put("lang ``", a -> IDENTITY);
		
		unsafe.put("identifier compare", v1 -> v2 -> compare(v1, v2) ? TRUE : FALSE);
		unsafe.put("identifier discover", v -> v instanceof ValueIdentifier ? TRUE : FALSE);
		unsafe.put("identifier resolve", RESOLVE);
		
		unsafe.put("lang Procedure", IDENTITY);
		unsafe.put("lang Environment", IDENTITY);
		unsafe.put("lang Package", SCOPE);
		unsafe.put("lang Scope", SCOPE);
		unsafe.put("lang Function", ident -> body -> arg -> body.call(env ->
			compare(env, ident)
				? arg
				: RESOLVE.call(env)
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
	public Value exists(String[] path) {
		if (path.length == 1) {
			path = new String[] {"unsafe", "lang", path[0]};
		}
		
		StringBuilder b = new StringBuilder ();
		
		for (int i = 1; i < path.length; i++) {
			b.append(path[i]);
			
			if (i + 1 < path.length) b.append(' ');
		}
		
		return unsafe.get(b.toString());
	}
}
