package parser.resolver;

import value.Value;

public abstract interface Resolver extends Value{
	public static final Value NULL = p -> Resolver.NULL;
}
