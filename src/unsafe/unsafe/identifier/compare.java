package unsafe.unsafe.identifier;

import value.Value;

public class compare implements Value {
	@Override
	public Value call(Value p) {
		return p2 -> Value.compare(p, p2) ? Value.TRUE : Value.FALSE;
	}
}
