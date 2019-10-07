package parser.token;

import node.Node;
import node.NodeBlock;
import node.NodeScope;
import parser.Stream;

public class TokenScope extends TokenBlock{
	public TokenScope (Stream s) {
		super(readBlock(s, ')'));
	}
	
	private TokenScope (Token[] tokens) {
		super(tokens);
	}
	
	@Override
	public String toString (){
		if (this.getTokens().length == 0) {
			return "()";
		}else if (this.getTokens().length == 1) {
			return "(" + this.getTokens()[0].toString() + ")";
		}else {
			return "(\n" + TokenFunction.indent(TokenBlock.toString(this, '\n')) + "\n)";
		}
	}
	
	@Override
	public Node createEvent() {
		return new NodeScope(new NodeBlock(this.createNodes()));
	}
	
	public static Token createBase (Stream s) {
		return new TokenScope(readBlock(s, '\0'));
	}
}
