package unsafe.unsafe;

import parser.token.TokenInteger;
import value.Value;
import value.ValueIdentifier;

public class console implements Value{
	@Override
	public Value call(Value v) {
		if (Value.compare(v, "put")) {
			return arg -> {
				System.out.write(TokenInteger.getInt(arg).intValue());
				return Value.NULL;
			};
		}else if (Value.compare(v, "ident")) {
			return ident -> {
				if (ident instanceof ValueIdentifier) {
					System.out.println(((ValueIdentifier) ident).name);
				}else {
					System.out.println("Not an identifier");
				}
				
				return Value.NULL;
			};
		}else if (Value.compare(v, "print")) {
			return p -> {
				System.out.println(p.toString() + (p == Value.NULL ? "[null]" : ""));
				return Value.NULL;
			};
		}
		
		return Value.NULL;
	}
	
}
