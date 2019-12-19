package node;

import value.Value;

public interface Node {
	public static final Node NULL = new Node() {
		
		@Override
		public Value run(Value environment) {
			return Value.NULL;
		}
		
		@Override
		public String toString (){
			return "{}";
		}
	};
	
	public Value run (Value environment);
}
