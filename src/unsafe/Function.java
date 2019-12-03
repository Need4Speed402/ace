package unsafe;

import value.Value;
import value.ValueIdentifier;

public class Function implements Value{

	@Override
	public Value call(Value v) {
		return v.call(e -> {
			if (e instanceof ValueIdentifier) {
				return ((ValueIdentifier) e).value;
			}else {
				return e;
			}
		});
	}

}
