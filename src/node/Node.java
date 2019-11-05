package node;

import value.Value;

public interface Node {
	public Value run (Value environment);
	
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
				e = new NodeScope(new NodeScope());
				
				for (int ii = nodes.length - 1; ii >= 0; ii--) {
					e = new NodeEnvironment(new NodeCall(new NodeCall(new NodeIdentifier("()"), nodes[ii]), e));
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
