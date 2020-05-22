package value.intrinsic;

import value.Value;
import value.ValueDefer;

public class Compare {
	public static final Value TRUE = ValueDefer.accept(p1 -> ValueDefer.accept(p2 -> p1));
	public static final Value FALSE = ValueDefer.accept(p1 -> ValueDefer.accept(p2 -> p2));
	
	public static final Value instance = a -> a.getID(aid -> b -> b.getID(bid -> aid == bid ? TRUE : FALSE));
}
