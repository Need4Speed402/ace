package parser.token;

import node.Node;
import node.NodeFunction;
import node.NodeBlock;
import node.NodeParameter;
import node.NodeScope;
import parser.Stream;

public class TokenFunction extends TokenBlock {
	public TokenFunction (Stream s) {
		super(readBlock(s, '}'));
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
	
	public Node createModifierEvent () {
		return this.createEvent(NodeParameter.MODIFIER);
	}
	
	public Node createHiddenEvent () {
		return this.createEvent(NodeParameter.NONE);
	}
	
	@Override
	public Node createEvent() {
		return this.createEvent(NodeParameter.PARAMETER);
	}
	
	private Node createEvent(NodeParameter.Type type) {
		Node[] nodes = this.createNodes();
		
		if (nodes.length == 0) {
			return new NodeBlock(nodes);
		}else {
			return new NodeFunction(new NodeScope(new NodeBlock(nodes)), type);
		}
	}
}
