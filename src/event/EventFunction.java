package event;

import java.util.ArrayList;
import java.util.List;

import parser.Global;
import parser.Local;
import parser.Node;
import parser.token.TokenFunction;
import value.Value;
import value.ValueFunction;
import value.ValueIdentifier;

public class EventFunction implements Event{
	public static long mem, scopes;
	
	private final Event[] contents;
	private final EventParamater.Type type;
	
	private EventFunction parent;
	public final Definition[] definitions;
	
	public EventFunction (Event[] contents, EventParamater.Type type) {
		this.contents = contents;
		this.type = type;
		
		ArrayList<EventIdentifier> idnt = new ArrayList<>();
		for (Event e : this.contents) e.indexIdentifiers(this, idnt);
		
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
		return new ValueFunction(this, local);
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
			EventFunction parent = this.parent;
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
		
		for (Event e : this.contents) e.init();
	}
	
	@Override
	public void indexIdentifiers(EventFunction scope, List<EventIdentifier> idnt) {
		this.parent = scope;
	}
	
	@Override
	public void paramaterHeight(Node<Integer> pHeight, Node<Integer> mHeight) {
		Node<Integer>
			np = pHeight.replace(pHeight.get(0) + 1),
			nm = mHeight.replace(mHeight.get(0) + 1);
		
		if (this.type == EventParamater.MODIFIER) {
			nm = nm.add(mHeight.get(0) + 1);
		}else if (this.type == EventParamater.PARAMATER) {
			np = np.add(pHeight.get(0) + 1);
		}
		
		for (Event e : this.contents) {
			e.paramaterHeight(np, nm);
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
			b.append('\t' + TokenFunction.indent(this.contents[i].toString()));
			
			if (i + 1 < this.contents.length) b.append('\n');
		}
		
		b.append("\n}");
		return b.toString();
	}
	
	public static Event createScope (Event[] contents) {
		return new EventCall(new EventFunction(contents, EventParamater.NONE), new EventFunction(new Event[] {}, EventParamater.NONE));
	}
}