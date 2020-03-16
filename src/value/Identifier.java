package value;

import parser.Stream;
import parser.token.syntax.TokenString;
import value.Value;

public class Identifier implements Value{
	private static int counter = 0;
	public final static Value NULL = p -> Identifier.NULL;
	
	public final int id;

	protected Identifier() {
		this.id = ++counter;
	}
	
	@Override
	public Value call(Value environment) {
		return environment.call(new Value () {
			@Override
			public Value call(Value v) {
				return NULL;
			}
			
			@Override
			public int getID() {
				return id;
			}
			
			@Override
			public String toString() {
				return "Identifier(" + Identifier.this.toString() + ")";
			}
		});
	}
	
	@Override
	public String toString() {
		String name = Value.ids_rev.get(this);
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
		if (obj instanceof Identifier) {
			return ((Identifier) obj).id == this.id;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
}
