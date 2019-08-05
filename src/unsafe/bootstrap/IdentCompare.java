package unsafe.bootstrap;

import value.Value;
import value.ValueIdentifier;

public class IdentCompare extends Value {
	public IdentCompare () {
		super (p1 -> {
			return new Value(p2 -> {
				if (p2 instanceof ValueIdentifier && p1.compare(((ValueIdentifier) p2).id)) {
					return new Value (p3 -> p3.call(Value.NULL));
				}
				
				return Value.NULL;
			});
		});
	}
}
