package parser.token;

import parser.ParserException;
import parser.Stream;
import parser.TokenList;

public class TokenBase extends TokenBlock {
	public TokenBase (Stream s) {
		super (TokenBase.readBlock(s, '\0'));
	}
	
	@Override
	public String toString() {
		return toString(this, '\n');
	}
	
	static Token[] readBlock (Stream s, char terminator) {
		TokenList tokens = new TokenList();
		
		boolean semiLegal = false;
		boolean semiUsed = false;
		
		while (true) {
			if (!s.hasChr()) {
				if (terminator != '\0') throw new ParserException("Unexpected end of input");
				break;
			}
			
			//comments
			if (s.next(";;")) {
				new TokenStatement(s);
				
				continue;
			}
			
			if (s.next(';')) {
				if (semiLegal) {
					semiLegal = false;
					semiUsed = true;
				}else {
					throw new ParserException("illegal location of semicolon");
				}
			}
			
			if (s.next('\n')) {
				if (semiUsed) {
					throw new ParserException ("illegal location of semicolon");
				}
				
				semiUsed = false;
				semiLegal = false;
			}
			
			if (s.next(Stream.whitespace)) continue;
			
			if (s.isNext(']', ')', '}')) {
				char next = s.chr();
				
				if (next == terminator) {
					break;
				}else {
					throw new ParserException("illegal location of closing block statement: " + next);
				}
			}
			
			tokens.push(new TokenStatement(s));
			semiLegal = true;
			semiUsed = false;
		}
		
		return tokens.toArray();
	}
}
