package node;

import value.Value;
import value.ValueIdentifier;

public class NodeBlock implements Node{
	private final Node[] contents;
	
	public NodeBlock(Node ... contents) {
		this.contents = contents;
	}
	
	@Override
	public Value run(Value environment) {
		for (int i = 0; i < this.contents.length; i++) {
			Value v = this.contents[i].run(environment);
			
			while (v instanceof ValueIdentifier) v = ((ValueIdentifier) v).getReference();
			
			if (v != Value.NULL) return v;
		}
		
		return Value.NULL;
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
