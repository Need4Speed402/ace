package value;

import value.Value;

public class Call implements Value{
	private Value function;
	private Value argument;
	
	protected Call (Value function, Value argument) {
		if (function == null || argument == null) throw new NullPointerException();
		
		this.function = function;
		this.argument = argument;
	}
	
	@Override
	public Value call(Value environment) {
		Value vf = this.function.call(environment);
		Value vp = this.argument.call(environment);
		
		return vf.call(vp);
	}
	
	@Override
	public String toString() {
		return "(" + this.function.toString() + " " + this.argument.toString() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Call) {
			Value f = ((Call) obj).function;
			Value a = ((Call) obj).argument;
			
			return f.equals(this.function) && a.equals(this.argument);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return (this.function.hashCode() ^ this.argument.hashCode()) + 11;
	}
}
