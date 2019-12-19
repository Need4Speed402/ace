package parser.token;

import node.Node;
import node.NodeCall;
import parser.Stream;

public class TokenArray extends TokenBlock{
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
			return "[\n" + TokenEnvironment.indent(TokenBlock.toString(this, '\n')) + "\n]";
		}
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall("Array", super.createEvent());
	}
}
