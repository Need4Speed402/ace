package parser.token.resolver;

import java.util.Arrays;
import java.util.HashSet;

import parser.token.Resolver;
import value.Value;

public class Virtual extends Resolver {
	protected final Resolver[] resolvers;
	
	public Virtual (String name, Resolver ... resolvers) {
		super(name);
		this.resolvers = resolvers;
	}
	
	public Resolver[] getResolvers() {
		return resolvers;
	}
	
	@Override
	public Value createNode() {
		Value proot = Value.id();
		
		Value set = Value.id();
		Value get = Value.id();
		
		Value out = Value.delegate(() -> {
			IdentifierResolver pairRoot = null;
			IdentifierResolver[] pairs;
			
			{
				HashSet<String> names = new HashSet<>();
				Resolver[] p = this.getResolvers();
				
				pairs = new IdentifierResolver[p.length];
				int len = 0;
				
				for (int i = 0; i < p.length; i++) {
					String name = p[i].getName();
					
					if (!names.contains(name)) {
						names.add(name);
						
						pairs[len++] = new IdentifierResolver(p[i]);
						if (name.equals("root")) pairRoot = pairs[len - 1];
					}
				}
				
				pairs = Arrays.copyOf(pairs, len);
			}
			
			Value genRoot, parent;
			
			if (this.getName().equals("root")) {
				genRoot = Value.call(get, Value.id());
				
				if (pairRoot != null) {
					parent = Value.call(get, Value.id(), Value.id("root"));
				}else{
					parent = proot;
				}
			}else if (pairRoot != null) {
				genRoot = Value.call(get, Value.id(), Value.id("root"));
				parent = Value.call(proot, Unsafe.PARENT, Value.id(this.getName()));
			} else {
				genRoot = proot;
				parent = Value.call(proot, Unsafe.PARENT, Value.id(this.getName()));
			}
			
			Value rel = Value.id();
			Value block = Value.call(parent, rel);
			
			for (IdentifierResolver p : pairs) {
				block = Value.call(Unsafe.SCOPE, Value.call(Unsafe.COMPARE, rel, p.identifier, Value.env(
					p.uniqueIdentifier
				), Value.env(
					block
				)));
			}
			
			block = Value.call(Unsafe.FUNCTION, rel, Value.env(block));
			
			for (IdentifierResolver p : pairs) {
				Value root, tparent;
				Value param = Value.id();
				
				if (p == pairRoot) {
					tparent = proot;
					root = Value.call(proot, param);
				}else {
					tparent = parent;
					root = Value.call(Unsafe.ASSIGN, param, Value.call(genRoot, param));
				}
				
				block = Value.call(Unsafe.FUNCTION, p.uniqueIdentifier, Value.env(block), Value.call(
					p.createNode(),
					Value.call(Unsafe.FUNCTION, param, Value.env(
						Value.call(Unsafe.SCOPE, Value.call(Unsafe.COMPARE, Unsafe.PARENT, param, Value.env(
							tparent
						), Value.env(
							root
						)))
					))
				));
			}
			
			return block;
		});
		
		Value arg = Value.id();
		
		return Value.call(Unsafe.FUNCTION, proot, Value.env(
			Value.call(Unsafe.MUTABLE, Value.id(),
				Value.call(Unsafe.FUNCTION, set, Value.env(
					Value.call(Unsafe.FUNCTION, get, Value.env(
						Value.call(Unsafe.DO,
							Value.call(set, Value.call(Unsafe.FUNCTION, arg, Value.env(
								Value.call(Value.call(set, out), arg)
							))),
							Value.call(Unsafe.FUNCTION, arg, Value.env(Value.call(get, Value.id(), arg)))
						)
					))
				))
			)
		));
	}

	public Virtual insert (Resolver r) {
		Resolver[] thispairs = this.getResolvers();
		
		for (int i = 0; i < thispairs.length; i++) {
			Resolver pair = thispairs[i];
			
			if (pair.getName().equals(r.getName())) {
				Resolver[] pairs = new Resolver[thispairs.length];
				System.arraycopy(thispairs, 0, pairs, 0, thispairs.length);
				pairs[i] = ((Virtual) pair).insert(r);
				
				return new Virtual(this.getName(), pairs);
			}
		}
		
		Resolver[] pairs = new Resolver[thispairs.length + 1];
		System.arraycopy(thispairs, 0, pairs, 0, thispairs.length);
		pairs[thispairs.length] = r;
		return new Virtual(this.getName(), pairs);
	}
	
	@Override
	public String toString() {
		Resolver[] children = this.getResolvers();
		
		if (children.length == 0) return "";
		
		StringBuilder b = new StringBuilder();
		
		b.append(this.getName()).append('\n');
		
		for (int i = 0; i < children.length; i++) {
			Resolver entry = children[i];
			
			boolean last = i == children.length - 1;
			
			if (last) b.append('\u2514');
			else b.append('\u251C');
			
			//b.append(' ').append(entry.name).append('\n');
			//b.append(last ? "  " : "\u2502 ");
			
			String val = entry.toString();
			
			for (int ii = 0; ii < val.length(); ii++) {
				char c = val.charAt(ii);
				
				if (c == '\n') {
					b.append(last ? "\n  " : "\n\u2502 ");
				}else {
					b.append(c);
				}
			}
			
			b.append("\n");
		}
		
		return b.substring(0, b.length() - 1);
	}
	
	private static class IdentifierResolver extends Resolver {
		public final Value uniqueIdentifier = Value.id();
		public final Value identifier;
		public final Resolver parent;
		
		public IdentifierResolver(Resolver parent) {
			super(parent.getName());
			this.parent = parent;
			
			this.identifier = Value.id(parent.getName());
		}
		
		@Override
		public Value createNode() {
			return this.parent.createNode();
		}
	}
}
