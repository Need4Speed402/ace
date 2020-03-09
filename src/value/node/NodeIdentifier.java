package value.node;

import parser.Stream;
import parser.token.TokenString;
import value.Value;

public class NodeIdentifier implements Node {
	private static int counter = 0;
	
	public final int id;
	
	protected NodeIdentifier() {
		this.id = ++counter;
	}
	
	@Override
	public Value run(Value environment) {
		return environment.call(new Value () {
			@Override
			public Value call(Value v) {
				return v;
			}
			
			@Override
			public int getID() {
				return id;
			}
			
			@Override
			public String toString() {
				return "Identifier(" + NodeIdentifier.this.toString() + ")";
			}
		});
	}
	
	@Override
	public String toString() {
		String name = Node.ids_rev.get(this);
		if (name == null) name = Integer.toString(this.id);
		
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
			return TokenString.readString (new Stream (name), '\0').toString();
		}else {
			return name;
		}
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
}
