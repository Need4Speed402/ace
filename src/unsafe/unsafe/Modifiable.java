package unsafe.unsafe;

import value.Value;

public class Modifiable implements Value{
	@Override
	public Value call(Value value) {
		return new Instance(value);
	}
	
	private class Instance implements Value {
		private Value containing;
		
		public Instance (Value containing) {
			this.containing = containing;
		}
		
		@Override
		public Value call(Value v) {
			if (Value.compare(v, ":")) {
				return set -> {
					this.containing = set;
					return Value.NULL;
				};
			}else if (Value.compare(v, "`*`")) {
				return this.containing;
			}else {
				return this.containing.call(v);
			}
		}
		
	}
}
