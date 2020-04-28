package value.node;

import parser.Stream;
import parser.token.syntax.TokenString;
import value.Value;
import value.ValueEffect;

public class NodeIdentifier implements Node{
	private static int counter = 0;
	public final static Value NULL = p -> new ValueEffect(NodeIdentifier.NULL, p);
	
	public final int id;

	protected NodeIdentifier() {
		this.id = ++counter;
	}
	
	@Override
	public Value run(Value environment) {
		return environment.call(new Value () {
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
				return "Identifier(" + asString(id) + ")";
			}
		});
	}
	
	@Override
	public String toString() {
		return asString(this.id);
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
}
