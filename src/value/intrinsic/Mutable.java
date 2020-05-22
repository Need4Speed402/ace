package value.intrinsic;

import value.Value;
import value.ValueDefer;
import value.ValueEffect;
import value.ValueProbe;
import value.effect.EffectSet;

public class Mutable {
	public static final Value instance = ValueDefer.accept(init -> {
		ValueProbe probe = new ValueProbe();
		
		return new ValueEffect(v -> v
			.call(ValueDefer.accept(s -> new ValueEffect(s, new EffectSet(probe, s))))
			.call(u -> new ValueEffect(probe, u))
		, new EffectSet(probe, init));
	});
}
