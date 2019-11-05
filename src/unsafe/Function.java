package unsafe;

import value.Value;
import value.ValueIdentifier;

public class Function implements Value{

	@Override
	public Value call(Value v) {
		return p -> {
			return v.call(env -> {
				if (Value.compare(env, ".")) {
					return p;
				}else {
					return ((ValueIdentifier) env).getReference();
				}
			});
		};
	}
	
}
