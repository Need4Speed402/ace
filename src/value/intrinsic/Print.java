package value.intrinsic;

import java.io.IOException;

import parser.token.syntax.TokenString;
import runtime.Effect;
import runtime.Runtime;
import value.Value;
import value.Value.Getter;
import value.ValueEffect;
import value.ValueFunction;
import value.node.NodeIdentifier;

public class Print implements Getter{
	public static final Value instance = new ValueFunction(message -> message.getID(new Print()));
	
	@Override
	public Value resolved(Value parent, int value) {
		return ValueEffect.create(parent, new EffectPrint(NodeIdentifier.asString(value)));
	}
	
	@Override
	public int complexity() {
		return 1;
	}
	
	@Override
	public String toString() {
		return "Print()";
	}
	
	@Override
	public String toString(Value ident) {
		return "Print(" + ident + ")";
	}
	
	public static class EffectPrint implements Effect {
		private final byte[] message;
		
		public EffectPrint (String message) {
			this.message = (message + "\n").getBytes();
		}
		
		@Override
		public void run(Runtime runtime) {
			try {
				runtime.out.write(this.message);
			} catch (IOException e) {}
		}
		
		@Override
		public String toString() {
			return "Print(" + new TokenString(new String(this.message, 0, this.message.length - 1)) + ")";
		}
	}

}
