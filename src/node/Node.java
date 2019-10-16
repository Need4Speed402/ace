package node;

import java.util.List;

import parser.LinkedNode;
import parser.Local;
import value.Value;

public interface Node {
	public Value run (Local local, LinkedNode<Value> parameters);
	
	public void indexIdentifiers(NodeScope scope, List<NodeIdentifier> idnt);
	public default void paramaterHeight (LinkedNode<Integer>[] nodes) {};
	
	public void init (Local global);
	
	public static Node pipe (Object ... objects) {
		Node ret = null;
		
		for (int i = objects.length - 1; i >= 0; i--) {
			Object o = objects[i];
			Node e = null;
			
			if (o instanceof String) {
				e = new NodeIdentifier((String) o);
			}else if (o instanceof Node) {
				e = (Node) o;
			}else if (o instanceof Node[]) {
				Node[] nodes = (Node[]) o;
				e = new NodeScope(new NodeBlock(new Node[] {}));
				
				for (int ii = nodes.length - 1; ii >= 0; ii--) {
					e = new NodeFunction(new NodeCall(new NodeCall(new NodeParameter(0, NodeParameter.NONE), nodes[ii]), e), NodeParameter.NONE);
				}
			}else {
				throw new IllegalArgumentException(o.toString() + " must be either a string or an event");
			}
			
			if (ret == null) {
				ret = e;
			}else {
				ret = new NodeCall(e, ret);
			}
		}
		
		return ret;
	}
}
