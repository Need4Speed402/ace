package value;

public class Value {
	public static final Value NULL = new Value(p -> Value.NULL);
	
	public Function function;
	
	public Value (Function function) {
		this.function = function;
	}
	
	public Value call (Value v) {
		return this.function.call(v);
	}
	
	public Value call (String v) {
		return this.function.call(new ValueIdentifier(v));
	}
	
	public boolean compare (String ident) {
		return false;
	}
	
	public interface Function {
		public Value call (Value v);
	}
}
