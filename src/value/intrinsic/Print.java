package value.intrinsic;

import value.Value;
import value.Value.Getter;
import value.ValueDefer;
import value.ValueEffect;
import value.ValueProbe;
import value.effect.EffectPrint;
import value.node.NodeIdentifier;

public class Print implements Getter{
	public static final Value instance = ValueDefer.accept(message -> message.getID(new Print(message)));
	
	private final Value message;
	
	private Print (Value message) {
		this.message = message;
	}

	@Override
	public Value resolved(int value) {
		return new ValueEffect(this.message, new EffectPrint(NodeIdentifier.asString(value)));
	}

	@Override
	public Getter resolve(ValueProbe probe, Value value) {
		return new Print(this.message.resolve(probe, value));
	}
}
