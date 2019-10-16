package node;

import java.util.List;

import parser.LinkedNode;
import parser.Local;
import value.Value;
import value.ValueFunction;

public class NodeFunction implements Node{
	private final Node contents;
	private final NodeParameter.Type type;
	
	public NodeFunction (Node contents, NodeParameter.Type type) {
		this.contents = contents;
		this.type = type;
	}
	
	@Override
	public Value run(Local local, LinkedNode<Value> parameters) {
		return new ValueFunction(this, local, parameters);
	}
	
	public Value run(Local local, LinkedNode<Value> parameters, Value paramater) {
		return this.contents.run(local, parameters.add(paramater));
	}
	
	@Override
	public void init(Local global) {
		this.contents.init(global);
	}
	
	@Override
	public void indexIdentifiers(NodeScope scope, List<NodeIdentifier> idnt) {
		this.contents.indexIdentifiers(scope, idnt);
	}
	
	@Override
	public void paramaterHeight(LinkedNode<Integer>[] nodes) {
		@SuppressWarnings("unchecked")
		LinkedNode<Integer>[] nn = new LinkedNode[nodes.length];
		
		for (int i = 0; i < nodes.length; i++) {
			if (i == this.type.ordinal()) {
				nn[i] = nodes[i].replace(nodes[i].get(0) + 1).add(nodes[i].get(0) + 1);
			}else {
				nn[i] = nodes[i].replace(nodes[i].get(0) + 1);
			}
		}
		
		this.contents.paramaterHeight(nn);
	}
	
	@Override
	public String toString() {
		return "{" + this.contents.toString() + "}";
	}
}