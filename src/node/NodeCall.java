package node;

import java.util.List;

import parser.Local;
import parser.LinkedNode;
import value.Value;

public class NodeCall implements Node{
	private Node function;
	private Node argument;
	
	public NodeCall (Node function, Node argument) {
		if (function == null || argument == null) throw new NullPointerException();
		
		this.function = function;
		this.argument = argument;
	}
	
	@Override
	public Value run(Local local) {
		return function.run(local).call(argument.run(local));
	}
	
	@Override
	public void init() {
		this.function.init();
		this.argument.init();
	}
	
	@Override
	public void indexIdentifiers(NodeFunction scope, List<NodeIdentifier> idnt) {
		this.function.indexIdentifiers(scope, idnt);
		this.argument.indexIdentifiers(scope, idnt);
	}
	
	@Override
	public void paramaterHeight(LinkedNode<Integer>[] nodes) {
		this.function.paramaterHeight(nodes);
		this.argument.paramaterHeight(nodes);
	}
	
	@Override
	public String toString() {
		return "(" + this.function.toString() + " " + this.argument.toString() + ")";
	}
}
