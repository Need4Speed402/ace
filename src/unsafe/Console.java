package unsafe;

import parser.token.TokenInteger;
import value.Value;

public class Console implements Value{

	@Override
	public Value call(Value v) {
		if (Value.compare(v, "put")) {
			return arg -> {
				System.out.write(TokenInteger.getInt(arg).intValue());
				return Value.NULL;
			};
		}else if (Value.compare(v, "test")) {
			System.out.println("Test print");
		}
		
		return Value.NULL;
	}
	
}
