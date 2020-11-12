package value.intrinsic;

import value.Value;
import value.ValueFunction;
import value.node.NodeIdentifier;

public class Compare implements Value {
	public static final Value TRUE = new ValueFunction(p1 -> new ValueFunction(p2 -> p1));
	public static final Value FALSE = new ValueFunction(p1 -> new ValueFunction(p2 -> p2));
	
	public static final Value instance = new Compare();
	private static final Getter firstGetterInstance = new FirstGetter();
	
	private static class FirstGetter implements Getter {
		@Override
		public Value resolved(Value parent, int id) {
			return b -> b.getID(new SecondGetter(id));
		}
		
		@Override
		public String toString() {
			return "Compare()";
		}
	}
	
	private static class SecondGetter implements Getter {
		private int checkID;
		
		public SecondGetter (int id) {
			this.checkID = id;
		}
		
		@Override
		public Value resolved(Value parent, int id) {
			return this.checkID == id ? TRUE : FALSE;
		}
		
		@Override
		public String toString() {
			return "Compare(" + NodeIdentifier.asString(this.checkID) + ")";
		}
		
		@Override
		public String toString(Value ident) {
			return "Compare(" + NodeIdentifier.asString(this.checkID) + " = " + ident + ")";
		}
	}

	@Override
	public Value call(Value a) {
		return a.getID(firstGetterInstance);
	}
	
	@Override
	public String toString() {
		return "Compare()";
	}
}
