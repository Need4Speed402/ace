package parser.token;

import node.Node;
import node.NodeCall;
import parser.Stream;

public class TokenEnvironment extends TokenBlock {
	public TokenEnvironment (Stream s) {
		super(readBlock(s, '}'));
	}
	
	public TokenEnvironment (Token ... contents) {
		super(contents);
	}
	
	public static String indent (String s) {
		StringBuilder ss = new StringBuilder();
		ss.append('\t');
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			
			if (c == '\n') {
				ss.append('\n').append('\t');
			}else {
				ss.append(c);
			}
		}
		
		return ss.toString();
	}
	
	@Override
	public String toString () {
		if (this.getLength() <= 1) {
			return "{" + TokenBlock.toString(this, '\n') + "}";
		}else {
			return "{\n" + indent(TokenBlock.toString(this, '\n')) + "\n}";
		}
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall("Environment", super.createEvent());
	}
}
