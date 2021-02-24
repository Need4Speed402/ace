package value.node;

import java.util.HashMap;

import value.Value;
import value.Value.CallReturn;
import value.node.NodeDelegate.Loader;

public interface Node {
	public CallReturn run (Value environment);
	
	public static final HashMap<NodeCall, NodeCall> calls = new HashMap<>();
	public static final HashMap<Node, NodeEnvironment> envs = new HashMap<>();
	
	public static final HashMap<String, NodeIdentifier> ids = new HashMap<>();
	public static final HashMap<Integer, String> ids_rev = new HashMap<>();

	public static NodeCall call (Node a, Node b) {
		NodeCall c = new NodeCall(a, b);
		NodeCall mem = calls.get(c);
		
		if (mem == null) {
			calls.put(c, c);
			mem = c;
		}
		
		return mem;
	}
	
	public static Node delegate (Loader l) {
		return new NodeDelegate(l);
	}
	
	public static Node env (Node a) {
		NodeEnvironment mem = envs.get(a);
		
		if (mem == null) {
			mem = new NodeEnvironment(a);
			envs.put(a, mem);
		}
		
		return mem;
	}
	
	public static NodeIdentifier id () {
		return new NodeIdentifier();
	}
	
	public static Node call (Node c, Node ... nodes) {
		for (int i = 0; i < nodes.length; i++) {
			c = Node.call(c, nodes[i]);
		}
		
		return c;
	}
	
	public static NodeIdentifier id (String ident) {
		NodeIdentifier mem = ids.get(ident);
		
		if (mem == null) {
			mem = Node.id();
			ids.put(ident, mem);
			ids_rev.put(mem.id, ident);
		}
		
		return mem;
	}
}
