package node;

import java.util.List;

import parser.LinkedNode;
import parser.Local;
import value.Value;
import value.ValueIdentifier;

public class NodeBlock implements Node{
	private final Node[] contents;
	
	public NodeBlock(Node[] contents) {
		this.contents = contents;
	}
	
	@Override
	public Value run(Local local, LinkedNode<Value> parameters) {
		for (int i = 0; i < this.contents.length; i++) {
			Value v = this.contents[i].run(local, parameters);
			
			while (v instanceof ValueIdentifier) v = ((ValueIdentifier) v).getReference();
			
			if (v != Value.NULL) return v;
		}
		
		return Value.NULL;
	}

	@Override
	public void indexIdentifiers(NodeScope scope, List<NodeIdentifier> idnt) {
		for (Node e : this.contents) e.indexIdentifiers(scope, idnt);
	}
	
	@Override
	public void init(Local global) {
		for (Node e : this.contents) e.init(global);
	}
	
	@Override
	public void paramaterHeight(LinkedNode<Integer>[] nodes) {
		for (Node e : this.contents) e.paramaterHeight(nodes);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < this.contents.length; i++) {
			b.append(this.contents[i].toString());
			
			if (i + 1 < this.contents.length) {
				b.append('\n');
			}
		}
		
		return b.toString();
	}
}
