package parser.token.syntax;

import parser.Stream;
import value.Value;

public class TokenBase extends TokenProcedure{
	public TokenBase(Stream s) {
		super(readBlock(s, '\0'));
	}
	
	@Override
	public Value createNode() {
		return Value.call(Value.id("Package"), super.createNode());
	}
}
