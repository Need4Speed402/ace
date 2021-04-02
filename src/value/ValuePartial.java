package value;

import runtime.Effect;
import runtime.Runtime;
import value.resolver.Resolver;

public abstract class ValuePartial implements Value {
	@Override
	public CallReturn call(Value arg) {
		Call c = new Call(this, arg);
		return new CallReturn (c, c);
	}
	
	@Override
	public int getID() {
		return -1;
	}
	
	public static class Probe extends ValuePartial {
		@Override
		public String toString() {
			return Value.printHash("Probe", this.hashCode());
		}
	} 
	
	public static class Call extends ValuePartial implements Effect{
		public final Value function, argument;
		
		public Call (Value function, Value argument) {
			this.function = function;
			this.argument = argument;
		}
		
		@Override
		public String toString() {
			return Value.printHash("Call", super.hashCode(), this.function, this.argument);
		}
		
		@Override
		public void run(Runtime runtime) {
			function.call(argument).effect.run(runtime);
		}
		
		@Override
		public CallReturn resolve(Resolver resolver) {
			return resolver.resolveValue(this.function).call(resolver.resolveValue(this.argument));
		}
		
		@Override
		public int hashCode() {
			return (this.function.hashCode() + this.argument.hashCode()) * 13;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Call) {
				Call c = (Call) obj;
				return c.function.equals(this.function) && c.argument.equals(this.argument);
			}
			return false;
		}
	}
}
