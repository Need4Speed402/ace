package unsafe;

import parser.token.TokenInteger;
import value.Value;

public class Memory implements Value{
	@Override
	public Value call(Value p) {
		return new Allocate(new Value[TokenInteger.getInt(p).intValue()]);
	}
	
	public static class Allocate implements Value{
		private final Value[] memory;
		
		public Allocate (Value[] memory) {
			this.memory = memory;
		}
		
		@Override
		public Value call(Value p) {
			if (Value.compare(p, "get")) {
				return p2 -> {
					int index = TokenInteger.getInt(p2).intValue();
					
					if (memory[index] == null) {
						return Value.NULL;
					}
					
					return memory[index];
				};
			}else if (Value.compare(p, "set")) {
				return p2 -> {
					int location = TokenInteger.getInt(p2).intValue();
					
					return p3 -> {
						memory[location] = p3;
						
						return Value.NULL;
					};
				};
			}
			
			return Value.NULL;
		}
	}
	
}
