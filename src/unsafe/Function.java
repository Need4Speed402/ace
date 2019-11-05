package unsafe;

import value.Value;
import value.ValueIdentifier;

public class Function implements Value{

	@Override
	public Value call(Value param) {
		return callback -> arg -> {
			return callback.call(env -> {
				if (Value.compare(env, param)) {
					return g -> {
						if (Value.compare(g, "`*`")) {
							return arg;
						}else {
							return arg.call(g);
						}
					};
				}else {
					return ((ValueIdentifier) env).getReference();
				}
			});
		};
	}
	
}
