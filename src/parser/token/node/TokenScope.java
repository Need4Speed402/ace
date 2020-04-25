package parser.token.node;

import parser.Stream;

public class TokenScope extends TokenBlock{
	public TokenScope (Stream s) {
		super(TokenBlock.readBlock(s, ')'));
	}
	
	public TokenScope (TokenBlock block) {
		super(block.getElements());
	}
}
