package unsafe.identifier;

import value.Value;

public class compare implements Value {
	@Override
	public Value call(Value p) {
		return p2 -> {
			if (Value.compare(p, p2)) {
				return p3 -> p3.call(Value.NULL);
			}
			
			return Value.NULL;
		};
	}
}
