package node;

import java.util.List;

import parser.Local;
import parser.LinkedNode;
import value.Value;

public interface Node {
	public Value run (Local local);
	public void indexIdentifiers(NodeFunction scope, List<NodeIdentifier> idnt);
	public default void paramaterHeight (LinkedNode<Integer>[] nodes) {};
	
	public default void init () {}
	
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
				e = new NodeIterator((Node[]) o);
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
