package event;

import java.util.ArrayList;
import java.util.List;

import parser.Global;
import parser.Local;
import parser.Node;
import parser.token.TokenFunction;
import value.Value;
import value.ValueIdentifier;

public class EventScope implements Event{
	protected Event contents;
	
	public static long mem;
	
	private EventScope parent;
	public final Definition[] definitions;
	
	public EventScope (Event contents) {
		this.contents = contents;
		
		ArrayList<EventIdentifier> idnt = new ArrayList<>();
		this.contents.indexIdentifiers(this, idnt);
		
		ArrayList<Definition> definitions = new ArrayList<>();
		
		for (int i = 0; i < idnt.size(); i++) {
			EventIdentifier ident = idnt.get(i);
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
		return this.run(local, null);
	}
	
	public Value run(Local local, Value paramater) {
		ValueIdentifier[] idents = new ValueIdentifier[this.definitions.length];
		mem += this.definitions.length;
		
		Local l = new Local(local, idents, paramater);
		
		for (int i = 0; i < this.definitions.length; i++) {
			Definition def = this.definitions[i];
			idents[i] = new ValueIdentifier(l.getParent(def.level).scope[def.index]);
		}
		
		return this.contents.run(l);
	}
	
	@Override
	public void init() {
		for (Definition def : this.definitions) {
			EventScope parent = this.parent;
			int level = 0;
			
			main:while (parent != null) {
				for (int i = 0; i < parent.definitions.length; i++) {
					Definition check = parent.definitions[i];
					
					if (check.name == def.name) {
						def.level = level + 1;
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
						def.level = level + 1;
						def.index = i;
						break;
					}
				}
			}
		}
		
		this.contents.init();
	}
	
	@Override
	public void indexIdentifiers(EventScope scope, List<EventIdentifier> idnt) {
		this.parent = scope;
	}
	
	@Override
	public void paramaterHeight(Node<Integer> pHeight, Node<Integer> mHeight) {
		this.contents.paramaterHeight(pHeight.replace(pHeight.get(0) + 1), mHeight.replace(mHeight.get(0) + 1));
	}
	
	public static class Definition {
		public String name;
		public int level = -1;
		public int index;
		
		public Definition (String name) {this.name = name;}
	}
	
	@Override
	public String toString() {
		return "(\n" + TokenFunction.indent(this.contents.toString()) + "\n)";
	}
}
