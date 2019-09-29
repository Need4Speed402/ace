package parser.token;

import node.Node;
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
			return "[\n" + TokenFunction.indent(TokenBlock.toString(this, '\n')) + "\n]";
		}
	}
	
	@Override
	public Node createEvent() {
		return Node.pipe("Array", "Iterator", this.createNodes());
	}
}
