package parser.token;

import node.Node;

public interface Modifier {
	public boolean isModifier ();
	public Node createNode ();
	
	public default Token bind (Token callee) {
		return () -> Node.call(this.createNode(), callee.createNode());
	}
}
