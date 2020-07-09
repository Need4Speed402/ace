package parser.token.resolver;

import java.util.Arrays;
import java.util.HashSet;

import parser.token.Resolver;
import value.node.Node;
import value.node.NodeIdentifier;

public class Virtual extends Resolver {
	protected final Resolver[] resolvers;
	
	public Virtual (String name, Resolver ... resolvers) {
		super(name);
		this.resolvers = resolvers;
	}
	
	public Resolver[] getResolvers() {
		return resolvers;
	}
	
	public Resolver getResolver (String name) {
		Resolver[] resolvers = this.getResolvers();
		
		for (int i = 0; i < resolvers.length; i++) {
			if (resolvers[i].getName().equals(name)) {
				return resolvers[i];
			}
		}
		
		return null;
	}
	
	@Override
	public Node createNode() {
		Node proot = Node.id();
		
		Node set = Node.id();
		Node get = Node.id();
		
		Node out = Node.delegate(() -> {
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
			
			Node genRoot, parent;
			
			if (this.getName().equals("root")) {
				genRoot = Node.call(get, Node.id());
				
				if (pairRoot != null) {
					parent = Node.call(get, Node.id(), Node.id("root"));
				}else{
					parent = proot;
				}
			}else if (pairRoot != null) {
				genRoot = Node.call(get, Node.id(), Node.id("root"));
				parent = Node.call(proot, Unsafe.PARENT, Node.id(this.getName()));
			} else {
				genRoot = proot;
				parent = Node.call(proot, Unsafe.PARENT, Node.id(this.getName()));
			}
			
			Node rel = Node.id();
			Node block = Node.call(parent, rel);
			
			for (IdentifierResolver p : pairs) {
				block = Node.call(Unsafe.COMPARE, rel, p.identifier, Node.env(
					p.uniqueIdentifier
				), Node.env(
					block
				), Unsafe.IDENTITY);
			}
			
			block = Node.call(Unsafe.FUNCTION, rel, Node.env(block));
			
			for (IdentifierResolver p : pairs) {
				Node root, tparent;
				Node param = Node.id();
				
				if (p == pairRoot) {
					tparent = proot;
					root = Node.call(proot, param);
				}else {
					tparent = parent;
					root = Node.call(Unsafe.ASSIGN, param, Node.call(genRoot, param));
				}
				
				block = Node.call(Unsafe.FUNCTION, p.uniqueIdentifier, Node.env(block), Node.call(
					p.createNode(),
					Node.call(Unsafe.FUNCTION, param, Node.env(
						Node.call(Unsafe.COMPARE, Unsafe.PARENT, param, Node.env(
							tparent
						), Node.env(
							root
						), Unsafe.IDENTITY)
					))
				));
			}
			
			return block;
		});
		
		Node arg = Node.id();
		
		return Node.call(Unsafe.FUNCTION, proot, Node.env(
			Node.call(Unsafe.MUTABLE, Node.id(),
				Node.call(Unsafe.FUNCTION, set, Node.env(
					Node.call(Unsafe.FUNCTION, get, Node.env(
						Node.call(Unsafe.DO,
							Node.call(set, Node.call(Unsafe.FUNCTION, arg, Node.env(
								Node.call(Node.call(set, out), arg)
							))),
							Node.call(Unsafe.FUNCTION, arg, Node.env(Node.call(get, Node.id(), arg)))
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
		public final NodeIdentifier uniqueIdentifier = Node.id();
		public final NodeIdentifier identifier;
		public final Resolver parent;
		
		public IdentifierResolver(Resolver parent) {
			super(parent.getName());
			this.parent = parent;
			
			this.identifier = Node.id(parent.getName());
		}
		
		@Override
		public Node createNode() {
			return this.parent.createNode();
		}
	}
}
