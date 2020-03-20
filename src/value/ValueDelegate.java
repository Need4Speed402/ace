package value;

import java.util.ArrayList;

import parser.Promise;

public class ValueDelegate implements Value{
	private ArrayList<Call> calls = new ArrayList<Call>();
	private final Promise<Integer> id = new Promise<Integer>();
	
	private Value resolved = null;
	
	public void resolve (Value v) {
		if (this.calls == null) throw new RuntimeException("already resolved");
		
		for (Call c : this.calls) {
			c.ret.resolve(v.call(c.argument));
		}
		
		this.resolved = v;
		this.id.resolve(v.getID());
		this.calls = null;
	}
	
	@Override
	public Value call(Value v) {
		if (this.calls == null) {
			return this.resolved.call(v);
		}else {
			Call c = new Call(v);
			this.calls.add(c);
			return c.ret;
		}
	}
	
	@Override
	public Promise<Integer> getID() {
		return this.id;
	}
	
	private static class Call {
		public final Value argument;
		public final ValueDelegate ret = new ValueDelegate();
		
		public Call (Value argument) {
			this.argument = argument;
		}
	}
	
	@Override
	public String toString() {
		return "Delegate(" + (this.calls == null ? this.resolved.toString() : "unresolved")  + ")";
	}
}
