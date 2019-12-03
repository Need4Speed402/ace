package unsafe;

import value.Value;

public class Environment implements Value{
	@Override
	public Value call(Value v) {
		return v;
	}
}
