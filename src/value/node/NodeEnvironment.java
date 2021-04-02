package value.node;

import parser.Color;
import value.Value;
import value.Value.CallReturn;
import value.ValueFunction;

public class NodeEnvironment implements Node{
	private final ValueFunction contents;
	
	public NodeEnvironment(Node contents) {
		this.contents = new ValueFunction(contents);
	}
	
	public CallReturn run(Value environment) {
		CallReturn ret =  new CallReturn(new ValueFunction(handler -> this.contents.call(new ValueFunction(var -> {
			CallReturn envCall = environment.call(var);
			CallReturn hCall = handler.call(envCall.value);
			return new CallReturn(hCall.value, envCall.effect, hCall.effect);
		}))));
		
		System.out.println(ret.value);
		return ret;
	}
	
	@Override
	public String toString() {
		return "{\n" + Color.indent(this.contents.toString()) + "\n}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NodeEnvironment) {
			return ((NodeEnvironment) obj).contents.equals(obj);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.contents.hashCode() + 7;
	}
}