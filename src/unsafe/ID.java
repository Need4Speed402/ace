package unsafe;

import parser.Global;
import value.Value;

public class ID extends Value{

	public ID () {
		super(p1 -> {
			return new IDValue();
		});
	}
	
	public static class IDValue extends Value {
		public IDValue () {
			super(null);
			this.function = p1 -> {
				return p1 == this ? Global.TRUE : Global.FALSE;
			};
		}
	}
}
