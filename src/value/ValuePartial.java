package value;

import value.resolver.Resolver;

public abstract class ValuePartial implements Value {
	@Override
	public Value call (Value arg) {
		return new Call(this, arg);
	}
	
	@Override
	public int getID() {
		return -1;
	}
	
	public static class Probe extends ValuePartial {
		@Override
		public Value resolve(Resolver res) {
			return res.get(this);
		}

		@Override
		public String toString() {
			int hash = this.hashCode();
			char[] chars = "0123456789ABCDEF".toCharArray();
			
			return "Probe " 
				+ chars[(hash >> 28) & 0xF]
				+ chars[(hash >> 24) & 0xF]
				+ chars[(hash >> 20) & 0xF]
				+ chars[(hash >> 16) & 0xF]
				+ chars[(hash >> 12) & 0xF]
				+ chars[(hash >> 8) & 0xF]
				+ chars[(hash >> 4) & 0xF]
				+ chars[(hash >> 0) & 0xF];
		}
	} 
	
	public static class Call extends ValuePartial {
		public final Value parent, argument;
		
		public Call (Value parent, Value argument) {
			this.parent = parent;
			this.argument = argument;
		}
		
		@Override
		public String toString() {
			return Value.print("Call", this.parent, this.argument);
		}
		
		@Override
		public Value resolve(Resolver res) {
			return res.cache(this.parent).call(res.cache(this.argument));
		}
	}
}
