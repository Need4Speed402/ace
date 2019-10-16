package unsafe;

import value.Value;

public class ID implements Value{
	
	@Override
	public Value call(Value v) {
		return new IDValue();
	}
	
	public static class IDValue implements Value {
		@Override
		public Value call(Value p) {
			return p == this ? p1 -> p2 -> p1 : p1 -> p2 -> p2;
		}
	}
}
