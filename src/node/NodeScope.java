package node;

import java.util.HashMap;

import parser.token.TokenEnvironment;
import value.Value;
import value.ValueIdentifier;

public class NodeScope implements Node{
	private final Node contents;
	
	public NodeScope (Node contents) {
		this.contents = contents;
	}
	
	public Value run(Value environment) {
		HashMap<String, Value> scope = new HashMap<String, Value>();
		
		return this.contents.run(env -> {
			String name = ((ValueIdentifier) env).id;
			
			return p -> {
				if (Value.compare(p, "`.`")) {
					return p2 -> {
						if (Value.compare(p2, "=")) {
							return p3 -> {
								scope.put(name, p3);
								return Value.NULL;
							};
						}else {
							return scope.getOrDefault(name, Value.NULL).call(p2);
						}
					};
				}
				
				Value ret = scope.get(name);
				if (ret == null) ret = environment.call(env);
				
				return ret.call(p);
			};
		});
	}

	@Override
	public String toString() {
		String contents = this.contents.toString();
		
		if (contents.isEmpty()){
			return "()";
		}else{
			return "(\n" + TokenEnvironment.indent(contents) + "\n)";
		}
	}
}