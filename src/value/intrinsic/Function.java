package value.intrinsic;

import value.Value;
import value.Value.Getter;
import value.ValueDefer;
import value.ValueProbe;

public class Function implements Getter{
	public static final Value instance = identv -> identv.getID(ident ->
		ValueDefer.accept(body -> ValueDefer.accept(arg ->
			body.call(env -> env.getID(new Function(arg, env, ident)))
		))
	);
	
	private final Value arg, env;
	private final int value;
	
	private Function (Value arg, Value env, int value) {
		this.arg = arg;
		this.env = env;
		this.value = value;
	}
	
	@Override
	public Value resolved(int value) {
		if (this.value == value) {
			return arg;
		}else {
			return env;
		}
	}
	
	@Override
	public Getter resolve(ValueProbe probe, Value value) {
		return new Function(this.arg.resolve(probe, value), this.env.resolve(probe, value), this.value);
	}
}
