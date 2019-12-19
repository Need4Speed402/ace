package unsafe;

import value.Value;

public class Block implements Value{
	@Override
	public Value call(Value v) {
		return Value.resolve(v);
	}
}
