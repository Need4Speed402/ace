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
		}else if (Value.compare(v, "print")) {
			return p -> {
				System.out.println(p.toString() + (p == Value.NULL ? "[null]" : ""));
				return Value.NULL;
			};
		}
		
		return Value.NULL;
	}
	
}
