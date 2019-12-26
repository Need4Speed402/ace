package node;

import java.util.HashMap;

import value.Value;

public interface Node {
	public Value run (Value environment);
	
	public static final Node NULL = new Node() {
		@Override
		public Value run(Value environment) {
			return Value.NULL;
		}
		
		@Override
		public String toString (){
			return "{}";
		}
	};
	
	public static final HashMap<NodeCall, NodeCall> calls = new HashMap<>();
	public static final HashMap<String, NodeIdentifier> ids = new HashMap<>();
	public static final HashMap<Node, NodeEnvironment> envs = new HashMap<>();
	
	/*public static Node call (Node a, Node b) {
		return new NodeCall(a, b);
	}
	
	public static Node env (Node a) {
		return new NodeEnvironment(a);
	}
	
	public static Node id (String id) {
		return new NodeIdentifier(id);
	}*/
	
	public static NodeCall call (Node a, Node b) {
		NodeCall c = new NodeCall(a, b);
		NodeCall mem = calls.get(c);
		
		if (mem == null) {
			calls.put(c, c);
			mem = c;
		}
		
		return mem;
	}
	
	public static Node env (Node a) {
		NodeEnvironment mem = envs.get(a);
		
		if (mem == null) {
			mem = new NodeEnvironment(a);
			envs.put(a, mem);
		}
		
		return mem;
	}
	
	public static Node id (String ident) {
		NodeIdentifier mem = ids.get(ident);
		
		if (mem == null) {
			mem = new NodeIdentifier(ident);
			ids.put(ident, mem);
		}
		
		return mem;
	}
	
	public static Node call (String a, Node ... nodes) {
		return Node.call(Node.id(a), nodes);
	}
	
	public static Node call (Node c, Node ... nodes) {
		for (int i = 0; i < nodes.length; i++) {
			c = Node.call(c, nodes[i]);
		}
		
		return c;
	}
}
