package unsafe;

import parser.Global;
import value.Value;

public class ID implements Value{
	
	@Override
	public Value call(Value v) {
		return new IDValue();
	}
	
	public static class IDValue implements Value {
		@Override
		public Value call(Value p) {
			return p == this ? Global.TRUE : Global.FALSE;
		}
	}
}
