package value.intrinsic;

import java.io.IOException;

import parser.token.syntax.TokenString;
import runtime.Effect;
import runtime.Runtime;
import value.Value;
import value.Value.CallReturn;
import value.ValueFunction;
import value.node.NodeIdentifier;
import value.resolver.Resolver;

public class Print {
	public static final Value instance = new ValueFunction(message -> new CallReturn(message, Print.create(message)));
	
	public static Effect create (Value message) {
		int id = message.getID();
		
		if (id == -1) {
			return new EffectUnknownPrint(message);
		}else {
			return new EffectPrint((NodeIdentifier.asString(id) + "\n").getBytes());
		}
	}
		
	public static class EffectUnknownPrint implements Effect {
		private final Value message;
		
		public EffectUnknownPrint (Value message) {
			this.message = message;
		}
		
		@Override
		public void run(Runtime runtime) {
			throw new Error("cannot run a print message that is not known. This is a bug");
		}
		
		@Override
		public Effect resolve(Resolver resolver) {
			return Print.create(this.message.resolve(resolver));
		}
		
		@Override
		public String toString() {
			return "Print(" + this.message + ")";
		}
	}
	
	public static class EffectPrint implements Effect {
		private final byte[] message;

		public EffectPrint (byte[] message) {
			this.message = message;
		}
		
		public byte[] getMessage () {
			return this.message;
		}
		
		@Override
		public void run(Runtime runtime) {
			try {
				runtime.out.write(this.getMessage());
			} catch (IOException e) {}
		}
		
		@Override
		public String toString() {
			return "Print(" + new TokenString(new String(this.message, 0, this.message.length - 1)) + ")";
		}
	}
}
