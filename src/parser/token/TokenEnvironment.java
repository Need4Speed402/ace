package parser.token;

import value.node.Node;
import parser.Stream;

public class TokenEnvironment extends TokenProcedure {
	public TokenEnvironment (Stream s) {
		super(readBlock(s, '}'));
	}
	
	public TokenEnvironment (Token ... contents) {
		super(contents);
	}
	
	public static String indent (String s) {
		StringBuilder ss = new StringBuilder();
		ss.append("  ");
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			
			if (c == '\n') {
				ss.append('\n').append("  ");
			}else {
				ss.append(c);
			}
		}
		
		return ss.toString();
	}
	
	@Override
	public String toString () {
		if (this.getLength() <= 1) {
			return "{" + TokenProcedure.toString(this, '\n') + "}";
		}else {
			return "{\n" + indent(TokenProcedure.toString(this, '\n')) + "\n}";
		}
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id("Environment"), super.createNode());
	}
}
