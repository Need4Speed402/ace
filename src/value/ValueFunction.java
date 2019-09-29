package value;

import node.NodeFunction;
import parser.LinkedNode;
import parser.Local;

public class ValueFunction implements Value{
	private final NodeFunction body;
	private final Local scope;
	private final LinkedNode<Value> parameters;
	
	public ValueFunction(NodeFunction body, Local scope, LinkedNode<Value> parameters) {
		this.body = body;
		this.scope = scope;
		this.parameters = parameters;
	}
	
	@Override
	public Value call(Value v) {
		return body.run(this.scope, parameters, v);
	}
}
