package unsafe.identifier;

import value.Value;
import value.ValueIdentifier;

public class IdentDiscover implements Value{

	@Override
	public Value call(Value v) {
		return v instanceof ValueIdentifier ? p1 -> p2 -> p1 : p1 -> p2 -> p2;
	}

}
