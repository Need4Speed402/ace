package unsafe.identifier;

import value.Value;
import value.ValueIdentifier;

public class discover implements Value{

	@Override
	public Value call(Value v) {
		return v instanceof ValueIdentifier ? Value.TRUE : Value.FALSE;
	}

}
