package node;

import java.util.ArrayList;
import java.util.List;

import parser.Global;
import parser.LinkedNode;
import parser.Local;
import parser.token.TokenFunction;
import value.Value;
import value.ValueIdentifier;

public class NodeScope implements Node{
	public static long mem, scopes;
	
	private final Node contents;
	private NodeScope parent;
	public final Definition[] definitions;
	
	public NodeScope (Node contents) {
		this.contents = contents;
		
		ArrayList<NodeIdentifier> idnt = new ArrayList<>();
		this.contents.indexIdentifiers(this, idnt);
		
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
	
	public Value run(Local local, LinkedNode<Value> parameters) {
		mem += this.definitions.length;
		scopes += 1;
		
		ValueIdentifier[] idents = new ValueIdentifier[this.definitions.length];
		
		for (int i = 0; i < this.definitions.length; i++) {
			Definition def = this.definitions[i];
			
			idents[i] = new ValueIdentifier(local.getParent(def.level).scope[def.index]);
		}
		
		return this.contents.run(new Local(local, idents), parameters);
	}
	
	@Override
	public void init() {
		for (Definition def : this.definitions) {
			NodeScope parent = this.parent;
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
		
		this.contents.init();
	}
	
	@Override
	public void indexIdentifiers(NodeScope scope, List<NodeIdentifier> idnt) {
		this.parent = scope;
	}
	
	@Override
	public void paramaterHeight(LinkedNode<Integer>[] nodes) {
		this.contents.paramaterHeight(nodes);
	}
	
	public static class Definition {
		public String name;
		public int level = -1;
		public int index;
		
		public Definition (String name) {this.name = name;}
	}
	
	@Override
	public String toString() {
		String contents = this.contents.toString();
		
		if (contents.isEmpty()){
			return "()";
		}else{
			return "(\n" + TokenFunction.indent(contents) + "\n)";
		}
	}
}