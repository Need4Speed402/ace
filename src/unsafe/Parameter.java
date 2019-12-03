package unsafe;

import value.Value;

public class Parameter implements Value{
	@Override
	public Value call(Value compare) {
		return constructor -> env -> arg -> constructor.call(ctx -> {
			if (Value.compare(compare, ctx)) {
				return g -> {
					if (Value.compare(g, "`*`")) {
						return arg;
					}else {
						return arg.call(g);
					}
				};
			}else {
				return env.call(ctx);
			}
		});
	}
}
