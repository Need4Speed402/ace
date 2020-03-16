package value;

import static parser.token.resolver.Unsafe.ASSIGN;
import static parser.token.resolver.Unsafe.COMPARE;
import static parser.token.resolver.Unsafe.CONSOLE;
import static parser.token.resolver.Unsafe.FUNCTION;
import static parser.token.resolver.Unsafe.MUTABLE;

public class DefaultEnironment implements Value{
	public static final Value TRUE = p1 -> p2 -> p1;
	public static final Value FALSE = p1 -> p2 -> p2;
	
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
					return "Assignment(" + name.toString() + ") -> " + value.toString();
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
	}
}
