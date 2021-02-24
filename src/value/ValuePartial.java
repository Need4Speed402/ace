package value;

import runtime.Effect;
import runtime.Runtime;
import value.resolver.Resolver;

public abstract class ValuePartial implements Value {
	@Override
	public CallReturn call(Value arg) {
		return new CallReturn (new Call(this, arg), new CallEffects(this, arg));
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
			return res.call(this.parent, this.argument).value;
		}
	}
	
	public static class CallEffects implements Effect {
		public final Value parent, argument;
		
		public CallEffects(Value parent, Value argument) {
			this.parent = parent;
			this.argument = argument;
		}

		@Override
		public void run(Runtime runtime) {
			parent.call(argument).effect.run(runtime);
		}
		
		@Override
		public Effect resolve(Resolver res) {
			return res.call(this.parent, this.argument).effect;
		}
		
		@Override
		public String toString() {
			return Value.print("CallEffects", this.parent, this.argument);
		}
	}
}
