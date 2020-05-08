package parser.token.syntax;

import value.node.Node;
import parser.ParserException;
import parser.Stream;
import parser.TokenList;
import parser.token.Token;

public class TokenProcedure implements Token{
	private Token[] tokens;
	
	public TokenProcedure (Token[] tokens) {
		this.tokens = tokens;
	}
	
	public TokenProcedure (Stream s, char terminator) {
		this.tokens = readBlock(s, terminator);
	}
	
	public int getLength () {
		return this.tokens.length;
	}
	
	public Token[] getTokens() {
		return tokens;
	}
	
	@Override
	public String toString() {
		return toString(this, '\n');
	}
	
	protected static String toString(TokenProcedure compound, char separator) {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < compound.tokens.length; i++) {
			b.append(compound.tokens[i].toString());
			
			if (i + 1 < compound.tokens.length) {
				b.append(separator);
			}
		}
		
		return b.toString();
	}
	
	@Override
	public Node createNode () {
		Node[] nodes = new Node[this.tokens.length];
		for (int i = 0; i < nodes.length; i++) nodes[i] = this.tokens[i].createNode();
		return createBlock(nodes);
	}
	
	public static Node createBlock (Node ... nodes) {
		Node current = Node.env(Node.id("`"));
		
		for (int i = nodes.length - 1; i >= 0; i--) {
			current = Node.env(Node.call(Node.id("``"), current, nodes[i]));
		}
		
		return Node.call(Node.id("Procedure"), current);
	}
	
	public static Token[] readBlock (Stream s, char terminator) {
		TokenList tokens = new TokenList();
		
		boolean semiLegal = false, semiUsed = false;
		
		while (true) {
			if (!s.hasChr()) {
				if (terminator != '\0') throw new ParserException("Unexpected end of input");
				break;
			}
			
			//comments
			if (!s.isNext(";;}", ";;)", ";;]", ";;;") && s.next(";;")) {
				if (!s.hasChr()) break;
				if (s.isNext(Stream.whitespace)) {
					while (s.hasChr() && !s.isNext('\n')) s.chr();
				}else{
					new TokenExpression(s);
				}
				
				continue;
			}
			
			if (s.next(';')) {
				if (!semiLegal) throw new ParserException("illegal location of semicolon");
				semiLegal = false;
				semiUsed = true;
				
				continue;
			}
			
			if (s.next('\n')) {
				if (semiUsed) throw new ParserException ("illegal location of semicolon");
				semiUsed = false;
				semiLegal = false;
				
				continue;
			}
			
			if (s.next(Stream.whitespace)) continue;
			
			if (s.isNext(']', ')', '}')) {
				char next = s.chr();
				
				if (next == terminator) {
					break;
				}else {
					throw new ParserException("illegal location of closing block token: " + next);
				}
			}
			
			tokens.push(new TokenExpression(s));
			semiLegal = true;
			semiUsed = false;
		}
		
		if (semiUsed) throw new ParserException("illegal location of semicolon");
		
		return tokens.toArray();
	}
}
