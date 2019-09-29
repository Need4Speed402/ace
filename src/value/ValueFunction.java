package value;

import node.NodeFunction;
import parser.Local;

public class ValueFunction implements Value{
	private final NodeFunction body;
	private final Local scope;
	
	public ValueFunction(NodeFunction body, Local scope) {
		this.body = body;
		this.scope = scope;
	}
	
	@Override
	public Value call(Value v) {
		return body.run(this.scope, v);
	}
}
