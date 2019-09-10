package unsafe.bootstrap;

import value.Value;

public class Modifiable implements Value{
	@Override
	public Value call(Value v) {
		return new MValue(v);
	}
	
	private static class MValue implements Value{
		private Value value;
		
		public MValue (Value value) {
			this.value = value;
		}
		
		@Override
		public Value call(Value p) {
			if (Value.compare(p, "set")) {
				return p2 -> {
					this.value = p2;
					return Value.NULL;
				};
			}else if (Value.compare(p, "get")) {
				return this.value;
			}
			
			return Value.NULL;
		}
	}
	
}
