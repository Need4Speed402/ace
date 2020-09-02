package value.intrinsic;

import value.Value;
import value.ValueFunction;

public class Compare {
	public static final Value TRUE = new ValueFunction(p1 -> new ValueFunction(p2 -> p1));
	public static final Value FALSE = new ValueFunction(p1 -> new ValueFunction(p2 -> p2));
	
	public static final Value instance = a -> a.getID(aid -> b -> b.getID(bid -> aid == bid ? TRUE : FALSE));
}
