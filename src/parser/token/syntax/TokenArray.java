package parser.token.syntax;

import parser.Stream;
import value.Value;

public class TokenArray extends TokenProcedure{
	public TokenArray(Stream s) {
		super(readBlock(s, ']'));
	}
	
	@Override
	public String toString () {
		if (this.getTokens().length == 0) {
			return "[]";
		}else if (this.getTokens().length == 1) {
			return "[" + this.getTokens()[0].toString() + "]";
		}else {
			return "[\n" + TokenEnvironment.indent(TokenProcedure.toString(this, '\n')) + "\n]";
		}
	}
	
	@Override
	public Value createNode() {
		return Value.call(Value.id("Array"), super.createNode());
	}
}
