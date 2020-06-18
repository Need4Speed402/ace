package value.intrinsic;

import value.Value;
import value.Value.Getter;
import value.Value.Resolver;
import value.ValueEffect;
import value.ValueFunction;
import value.effect.EffectPrint;
import value.node.NodeIdentifier;

public class Print implements Getter{
	public static final Value instance = new ValueFunction(message -> message.getID(new Print(message)));
	
	private final Value message;
	
	private Print (Value message) {
		this.message = message;
	}

	@Override
	public Value resolved(int value) {
		return new ValueEffect(this.message, new EffectPrint(NodeIdentifier.asString(value)));
	}

	@Override
	public Getter resolve(Resolver res) {
		return new Print(this.message.resolve(res));
	}
}
