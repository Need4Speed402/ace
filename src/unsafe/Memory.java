package unsafe;

import parser.token.TokenInteger;
import value.Value;

public class Memory extends Value{
	public Memory() {
		super(p1 -> {
			return new Allocate(new Value[TokenInteger.getInt(p1).intValue()]);
		});
	}
	
	public static class Allocate extends Value{
		public Allocate (Value[] memory) {
			super(p2 -> {
				if (p2.compare("get")) {
					return new Value(p3 -> {
						int index = TokenInteger.getInt(p3).intValue();
						
						return memory[index];
					});
				}else if (p2.compare("set")) {
					return new Value (p3 -> {
						int location = TokenInteger.getInt(p3).intValue();
						
						return new Value(p4 -> {
							memory[location] = p4;
							
							return Value.NULL;
						});
					});
				}
				
				return Value.NULL;
			});
		}
	}
	
}
