package node;

import java.util.List;

import parser.Local;
import parser.LinkedNode;
import value.Value;

public class NodeIterator implements Node{
	private final Node[] elements;
	
	public NodeIterator(Node[] elements) {
		this.elements = elements;
	}

	@Override
	public Value run(Local local) {
		Value v = Value.NULL;
		
		for (int i = this.elements.length - 1; i >= 0; i--) {
			Value vv = v;
			Value element = this.elements[i].run(local);
			
			v = p -> p.call(element).call(vv);
		}
		
		return v;
	}

	@Override
	public void indexIdentifiers(NodeFunction scope, List<NodeIdentifier> idnt) {
		for (Node e : this.elements) e.indexIdentifiers(scope, idnt);
	}
	
	@Override
	public void paramaterHeight(LinkedNode<Integer>[] nodes) {
		for (Node e : this.elements) e.paramaterHeight(nodes);
	}
	
	@Override
	public void init() {
		for (Node e : this.elements) e.init();
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		
		for (int i = 0; i < this.elements.length; i++) {
			b.append(this.elements[i]);
			
			if (i + 1 < this.elements.length) {
				b.append("; ");
			}
		}
		
		b.append("]");
		return b.toString();
	}
}
