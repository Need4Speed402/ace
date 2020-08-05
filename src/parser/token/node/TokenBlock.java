package parser.token.node;

import parser.ParserException;
import parser.Stream;
import parser.TokenList;
import parser.token.Token;
import parser.token.syntax.TokenBaseEntry;
import parser.token.syntax.TokenExpression;
import parser.token.syntax.TokenString;
import value.node.Node;

public abstract class TokenBlock implements Token{
	private final Token[] elements;
	
	public TokenBlock (Token[] tokens) {
		this.elements = tokens;
	}
	
	public Token[] getElements() {
		return elements;
	}
	
	@Override
	public Node createNode() {
		Node n = this.elements[0].createNode();
		
		for (int i = 1; i < this.elements.length; i++) {
			n = Node.call(n, this.elements[i].createNode());
		}
		
		return n;
	}
	
	private static Token readOnce (Stream s) {
		//comments
		if (!s.isNext(";;}", ";;)", ";;]", ";;;") && s.next(";;")) {
			if (!s.hasChr()) return null;
			if (s.isNext(Stream.whitespace)) {
				while (s.hasChr() && !s.isNext('\n')) s.chr();
			}else{
				new TokenExpression(s);
			}
			
			return null;
		}
		
		if (s.next(Stream.whitespace)) return null;
		if (s.next('(')) return new TokenScope(s);
		if (s.next('{')) return new TokenEnvironment(s);
		if (s.next('[')) {
			if (s.next('[')) {
				Token t = new TokenBaseEntry(s, ']');
				if (!s.next(']')) throw new ParserException("Expected extra closing bracket ']' to match with opening syntax literal '[['");
				return t;
			}else{
				return new TokenBuiltin(s);
			}
		}
		if (s.next('"')) return new TokenIdentifier(TokenString.readEscapedString(s));
		if (s.next('\'')) return new TokenIdentifier(TokenString.readString(s));
		
		return new TokenIdentifier(s);
	}
	
	public static Token[] readBlock (Stream s, char terminator) {
		TokenList tokens = new TokenList();
		
		while (true) {
			if (!s.hasChr()) {
				if (terminator != '\0') throw new ParserException("Unexpected end of input");
				break;
			}
			
			if (s.isNext(']', ')', '}')) {
				char next = s.chr();
				
				if (next == terminator) {
					break;
				}else {
					throw new ParserException("illegal location of closing block token: " + next);
				}
			}
			
			Token t = readOnce(s);
			if (t != null) tokens.push(t);
		}
		
		return tokens.toArray();
	}
}
