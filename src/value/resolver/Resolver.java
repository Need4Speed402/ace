package value.resolver;

import parser.ProbeSet;
import value.Value;
import value.ValuePartial.Probe;

public abstract class Resolver {
	public abstract Value get (Probe p);
	public abstract boolean has (ProbeSet set);
}
