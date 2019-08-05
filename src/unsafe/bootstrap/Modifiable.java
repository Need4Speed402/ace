package unsafe.bootstrap;

import value.Value;

public class Modifiable extends Value{
	public Modifiable() {
		super(p1 -> new MValue(p1));
	}
	
	private static class MValue extends Value{
		public Value value;
		
		public MValue (Value value) {
			super(null);
			
			this.function = p2 -> {
				if (p2.compare("set")) {
					return new Value(p3 -> {
						this.value = p3;
						return Value.NULL;
					});
				}else if (p2.compare("get")) {
					return this.value;
				}
				
				return Value.NULL;
			};
			
			this.value = value;
		}
	}
	
}
