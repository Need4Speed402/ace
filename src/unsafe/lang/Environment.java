package unsafe.lang;

import value.Value;

public class Environment implements Value{
	@Override
	public Value call(Value v) {
		return val -> {
			if (Value.compare(val, "`,")) {
				return body -> env -> arg -> body.call(env);
			}
			
			return v.call(val);
		};
	}
}
