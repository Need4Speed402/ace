package parser.token.syntax;

import parser.Color;
import parser.Stream;
import parser.token.Modifier;
import parser.token.Token;
import value.node.Node;

public class TokenScope extends TokenProcedure implements Modifier{
	public TokenScope (Stream s) {
		super(readBlock(s, ')'));
	}
	
	public TokenScope (Token ... tokens) {
		super(tokens);
	}
	
	@Override
	public String toString (){
		if (this.getTokens().length == 0) {
			return "()";
		}else if (this.getTokens().length == 1) {
			return "(" + this.getTokens()[0].toString() + ")";
		}else {
			return "(\n" + Color.indent(TokenProcedure.toString(this, '\n')) + "\n)";
		}
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id("Scope"), super.createNode());
	}
	
	@Override
	public boolean isModifier() {
		Token[] tokens = this.getTokens();
		if (tokens.length != 1) return false;
		if (!(tokens[0] instanceof TokenExpression)) return false;
		
		return ((TokenExpression) tokens[0]).isModifier();
	}
}
