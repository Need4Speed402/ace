package value;

import parser.token.syntax.TokenEnvironment;
import value.Value;

public class Environment implements Value{
	private final Value contents;
	
	protected Environment(Value contents) {
		this.contents = contents;
	}
	
	public Value call(Value environment) {
		return var -> this.contents.call(env -> var.call(environment.call(env)));
	}
	
	@Override
	public String toString() {
		return "{\n" + TokenEnvironment.indent(this.contents.toString()) + "\n}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Environment) {
			return ((Environment) obj).contents.equals(obj);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.contents.hashCode() + 7;
	}
}