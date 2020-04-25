package parser.token.node;

import parser.Stream;
import value.node.Node;

public class TokenEnvironment extends TokenBlock{
	public TokenEnvironment (Stream s) {
		super(TokenBlock.readBlock(s, '}'));
	}
	
	public TokenEnvironment (TokenBlock block) {
		super(block.getElements());
	}
	
	@Override
	public Node createNode() {
		return Node.env(super.createNode());
	}
}
