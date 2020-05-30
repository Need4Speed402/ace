package value.intrinsic;

import value.Value;
import value.ValueDefer;
import value.ValueEffect;
import value.ValueProbe;
import value.effect.EffectSet;

public class Mutable {
	public static final Value instance = init -> {
		ValueProbe probe = new ValueProbe();
		
		return new ValueEffect(v -> v
			.call(ValueDefer.accept(s -> new ValueEffect(s, new EffectSet(probe, s))))
			.call(ValueDefer.accept(s -> probe))
		, new EffectSet(probe, init));
	};
}
