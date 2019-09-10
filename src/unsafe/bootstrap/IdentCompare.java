package unsafe.bootstrap;

import value.Value;
import value.ValueIdentifier;

public class IdentCompare implements Value {
	@Override
	public Value call(Value p) {
		return p2 -> {
			if (p instanceof ValueIdentifier && p2 instanceof ValueIdentifier &&
					((ValueIdentifier) p).id == ((ValueIdentifier) p2).id) {
				return p3 -> p3.call(Value.NULL);
			}
			
			return Value.NULL;
		};
	}
}
