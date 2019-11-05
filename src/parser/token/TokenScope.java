package parser.token;

import node.Node;
import node.NodeScope;
import parser.Stream;

public class TokenScope extends TokenBlock implements Modifier{
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
			return "(\n" + TokenEnvironment.indent(TokenBlock.toString(this, '\n')) + "\n)";
		}
	}
	
	@Override
	public Node createEvent() {
		return new NodeScope(this.createNodes());
	}
	
	public static Token createBase (Stream s) {
		return new TokenScope(readBlock(s, '\0'));
	}
	
	@Override
	public boolean isModifier() {
		Token[] tokens = this.getTokens();
		if (tokens.length != 1) return false;
		if (!(tokens[0] instanceof TokenStatement)) return false;
		
		return ((TokenStatement) tokens[0]).isModifier();
	}
}
