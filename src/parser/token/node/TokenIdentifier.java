package parser.token.node;

import java.util.Arrays;

import parser.ParserException;
import parser.Stream;
import parser.token.Token;
import parser.token.syntax.TokenString;
import value.node.Node;

public class TokenIdentifier implements Token{
	private final String identifier;
	
	public TokenIdentifier (String s) {
		this.identifier = s;
	}
	
	public TokenIdentifier(Stream s) {
		this.identifier = readIdentifier (s);
	}
	
	public TokenIdentifier (TokenString token) {
		Token[] tokens = token.getTokens();
		
		if (tokens.length != 1) {
			throw new ParserException("String must have no nested runtime blocks");
		}else {
			this.identifier = tokens[0].toString();
		}
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public Node createNode() {
		return Node.id(this.identifier);
	}
	
	public static String readIdentifier (Stream s) {
		StringBuilder b = new StringBuilder();
		
		while (s.hasChr()) {
			if (s.isNext(Stream.whitespace)) break;
			if (s.isNext("[{()}]\"';".toCharArray())) break;
			b.append(s.chr());
		}
		
		return b.toString();
	}
}
