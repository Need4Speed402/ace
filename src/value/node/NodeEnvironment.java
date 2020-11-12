package value.node;

import parser.Color;
import parser.ProbeSet.ProbeContainer;
import value.Value;
import value.ValueFunction;

public class NodeEnvironment implements Node{
	private final Node contents;
	private final ProbeContainer[] resolvers;
	
	protected NodeEnvironment(Node contents) {
		this.contents = contents;
		this.resolvers = null;
	}
	
	public NodeEnvironment (Node contents, ProbeContainer ... resolvers) {
		this.contents = contents;
		this.resolvers = resolvers;
	}
	
	public Value run(Value environment) {
		//return new ValueFunction(env -> new ValueFunction( 
		//	new Context(this.contents, env)
		//)).call(environment);
		return new ValueFunction(new Context(this.contents, environment));
	}
	
	@Override
	public String toString() {
		return "{\n" + Color.indent(this.contents.toString()) + "\n}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.resolvers != null) return false;
		
		if (obj instanceof NodeEnvironment) {
			return ((NodeEnvironment) obj).contents.equals(obj);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.contents.hashCode() + 7;
	}
	
	private static class Context implements Node {
		private final Node contents;
		private final Value environment;
		
		public Context (Node contents, Value environment) {
			this.contents = contents;
			this.environment = environment;
		}
		
		@Override
		public Value run(Value handler) {
			return new ValueFunction (this.contents).call(new ValueFunction(var -> {
				return handler.call(this.environment.call(var));
			}));
		}
		
		@Override
		public String toString() {
			return this.contents.toString();
		}
	}
}