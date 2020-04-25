package parser.token.node;

import parser.Stream;

public class TokenBase extends TokenBlock{
	public TokenBase (Stream s) {
		super(TokenBlock.readBlock(s, '\0'));
	}
}
