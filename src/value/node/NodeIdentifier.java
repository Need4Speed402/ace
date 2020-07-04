package value.node;

import parser.Stream;
import parser.token.syntax.TokenString;
import value.Value;
import value.ValueFunction;
import value.ValuePartial.Probe;

public class NodeIdentifier implements Node, Value {
	public final static Value NULL = new Null();
	
	private static int counter = 0;
	public final int id;

	protected NodeIdentifier() {
		this.id = ++counter;
	}
	
	@Override
	public Value run(Value environment) {
		return environment.call(this);
	}
	
	@Override
	public Value call(Value v) {
		return NULL.call(v);
	}
	
	@Override
	public Value getID(Getter getter) {
		return getter.resolved(id);
	}
	
	@Override
	public String toString() {
		return "NodeIdentifier(" + asString(this.id) + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NodeIdentifier) {
			return ((NodeIdentifier) obj).id == this.id;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	public static String asString (int id) {
		String name = Node.ids_rev.get(id);
		if (name == null) name = Integer.toString(id);
		
		boolean isSpecial = name.isEmpty();
		
		if (!isSpecial) {
			Stream s = new Stream(name);
			
			while (s.hasChr()) {
				if (s.next(Stream.whitespace) || s.next("{}[]();\"\'".toCharArray())) {
					isSpecial = true;
					break;
				}
				
				s.chr();
			}
		}
		
		if (isSpecial) {
			return new TokenString (name).toString();
		}else {
			return name;
		}
	}
	
	private static class Null extends ValueFunction {
		public Null () {
			super (v -> NodeIdentifier.NULL);
		}
		
		@Override
		public Value resolve(Probe probe, Value value) {
			return this;
		}
		
		@Override
		public String toString() {
			return "NodeIdentifier.Null";
		}
	}
}
