package node;

import java.util.ArrayList;
import java.util.List;

import parser.Global;
import parser.Local;
import parser.LinkedNode;
import parser.token.TokenFunction;
import value.Value;
import value.ValueFunction;
import value.ValueIdentifier;

public class NodeFunction implements Node{
	public static long mem, scopes;
	
	private final Node[] contents;
	private final NodeParameter.Type type;
	
	private NodeFunction parent;
	public final Definition[] definitions;
	
	public NodeFunction (Node[] contents, NodeParameter.Type type) {
		this.contents = contents;
		this.type = type;
		
		ArrayList<NodeIdentifier> idnt = new ArrayList<>();
		for (Node e : this.contents) e.indexIdentifiers(this, idnt);
		
		ArrayList<Definition> definitions = new ArrayList<>();
		
		for (int i = 0; i < idnt.size(); i++) {
			NodeIdentifier ident = idnt.get(i);
			int found = -1;
			
			//remove duplicates
			for (int ii = 0; ii < definitions.size(); ii++) {
				Definition def = definitions.get(ii);
				
				if (def.name == ident.name) {
					found = ii;
					break;
				}
			}
			
			if (found == -1) {
				ident.location = definitions.size();
				definitions.add(new Definition(ident.name));
			}else {
				ident.location = found;
			}
		}
		
		this.definitions = definitions.toArray(new Definition[definitions.size()]);
	}
	
	@Override
	public Value run(Local local) {
		if (this.contents.length == 0) {
			return Value.NULL;
		}else {
			return new ValueFunction(this, local);
		}
	}
	
	public Value run(Local local, Value paramater) {
		ValueIdentifier[] idents = new ValueIdentifier[this.definitions.length];
		mem += this.definitions.length;
		scopes += 1;
		
		for (int i = 0; i < this.definitions.length; i++) {
			Definition def = this.definitions[i];
			idents[i] = new ValueIdentifier(local.getParent(def.level).scope[def.index]);
		}
		
		Local scope = new Local(local, idents, paramater);
		
		for (int i = 0; i < this.contents.length; i++) {
			Value v = this.contents[i].run(scope);
			
			if (v instanceof ValueIdentifier) {
				v = ((ValueIdentifier) v).getReference();
			}
			
			if (v != Value.NULL) return v;
		}
		
		return Value.NULL;
	}
	
	@Override
	public void init() {
		for (Definition def : this.definitions) {
			NodeFunction parent = this.parent;
			int level = 0;
			
			main:while (parent != null) {
				for (int i = 0; i < parent.definitions.length; i++) {
					Definition check = parent.definitions[i];
					
					if (check.name == def.name) {
						def.level = level;
						def.index = i;
						break main;
					}
				}
				
				level++;
				parent = parent.parent;
			}
			
			//look in global scope
			if (def.level < 0) {
				ValueIdentifier[] idents = Global.global.scope;
				
				for(int i = 0; i < idents.length; i++) {
					if (idents[i].id == def.name) {
						def.level = level;
						def.index = i;
						break;
					}
				}
			}
		}
		
		for (Node e : this.contents) e.init();
	}
	
	@Override
	public void indexIdentifiers(NodeFunction scope, List<NodeIdentifier> idnt) {
		this.parent = scope;
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
		
		for (Node e : this.contents) {
			e.paramaterHeight(nn);
		}
	}
	
	public static class Definition {
		public String name;
		public int level = -1;
		public int index;
		
		public Definition (String name) {this.name = name;}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder ();
		b.append("{\n");
		
		for (int i = 0; i < this.contents.length; i++) {
			b.append(TokenFunction.indent(this.contents[i].toString()));
			
			if (i + 1 < this.contents.length) b.append('\n');
		}
		
		b.append("\n}");
		return b.toString();
	}
	
	public static Node createScope (Node[] contents) {
		if (contents.length == 0) {
			return new NodeFunction(new Node[] {}, NodeParameter.NONE);
		}else {
			return new NodeCall(new NodeFunction(contents, NodeParameter.NONE), new NodeFunction(new Node[] {}, NodeParameter.NONE));
		}
	}
}