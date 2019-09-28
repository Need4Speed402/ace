package value;

import event.EventFunction;
import parser.Local;

public class ValueFunction implements Value{
	private final EventFunction body;
	private final Local scope;
	
	public ValueFunction(EventFunction body, Local scope) {
		this.body = body;
		this.scope = scope;
	}
	
	@Override
	public Value call(Value v) {
		return body.run(this.scope, v);
	}
}
