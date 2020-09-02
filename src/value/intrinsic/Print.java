package value.intrinsic;

import java.io.IOException;

import parser.ProbeSet;
import runtime.Effect;
import runtime.Runtime;
import value.Value;
import value.Value.Getter;
import value.ValueEffect;
import value.ValueFunction;
import value.node.NodeIdentifier;
import value.resolver.Resolver;

public class Print implements Getter{
	public static final Value instance = new ValueFunction(message -> message.getID(new Print(message)));
	
	private final Value message;
	
	private Print (Value message) {
		this.message = message;
	}

	@Override
	public Value resolved(int value) {
		return ValueEffect.create(this.message, new EffectPrint(NodeIdentifier.asString(value)));
	}

	@Override
	public Getter resolve(Resolver res) {
		Value m = this.message.resolve(res);
		
		if (m == this.message) {
			return this;
		}else {
			return new Print(m);
		}
	}
	
	@Override
	public void getResolves(ProbeSet set) {
		this.message.getResolves(set);
	}
	
	@Override
	public String toString() {
		return super.toString() + " <- " + this.message;
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
			return "Print(" + new String(this.message, 0, this.message.length - 1) + ")";
		}
	}

}
