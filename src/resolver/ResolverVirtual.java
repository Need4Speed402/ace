package resolver;

import java.util.Arrays;
import java.util.HashSet;

import parser.Color;
import value.Unsafe;
import value.node.Node;
import value.node.NodeIdentifier;

public class ResolverVirtual extends Resolver {
	protected final Pair[] pairs;
	
	public ResolverVirtual (Pair ... pairs) {
		this.pairs = pairs;
	}
	
	public Pair[] getPairs (){
		return this.pairs;
	}
	
	@Override
	public Node createNode(boolean isRoot) {
		IdentifierPair pairRoot = null;
		IdentifierPair[] pairs;
		
		{
			HashSet<String> names = new HashSet<>();
			Pair[] p = this.getPairs();
			
			pairs = new IdentifierPair[p.length];
			int len = 0;
			
			for (int i = 0; i < p.length; i++) {
				String name = p[i].name;
				
				if (!names.contains(name)) {
					names.add(name);
					
					pairs[len++] = new IdentifierPair(p[i]);
					if (name.equals("root")) pairRoot = pairs[len - 1];
				}
			}
			
			pairs = Arrays.copyOf(pairs, len);
		}
		
		Node rel = Node.id();
		Node root = Node.id();
		Node parent = Node.id();
		Node block = rel;
		
		for (IdentifierPair p : pairs) {
			block = Node.call(Unsafe.SCOPE, Node.call(Unsafe.COMPARE, rel, p.identifier, Node.env(
				p.uniqueIdentifier
			), Node.env(
				block
			)));
		}
		
		if (pairRoot != null) {
			Node param = Node.id();
			
			block = Node.call(Unsafe.SCOPE, Node.call(Unsafe.COMPARE, rel, Unsafe.ROOT, Node.env(
				Node.call(Unsafe.FUNCTION, param, Node.env(
					Node.call(pairRoot.uniqueIdentifier, Node.call(pairRoot.uniqueIdentifier, Unsafe.ROOT, param))
				))
			), Node.env(
				block
			)));
		}
		
		block = Node.call(Unsafe.FUNCTION, rel, Node.env(block));
		
		for (IdentifierPair p : pairs) {
			Node tparent;
			
			if (pairRoot == null) {
				tparent = Node.call(parent, p.identifier);
			}else if (pairRoot == p) {
				tparent = parent;
			}else {
				tparent = Node.call(root, p.identifier);
			}
			
			block = Node.call(
				Unsafe.FUNCTION,
				p.uniqueIdentifier,
				Node.env(block),
				Node.call(Unsafe.ASSIGN, p.identifier, Node.call(p.resolver.createNode(pairRoot == p), tparent, root))
			);
		}
		
		if (pairRoot != null) {
			Node set = Node.id();
			Node get = Node.id();
			Node param = Node.id();
			Node proot = Node.id();	
			
			block = Node.call(Unsafe.FUNCTION, proot, Node.env(
				Node.call(Unsafe.MUTABLE, Unsafe.DO, Node.call(Unsafe.FUNCTION, set, Node.env(
					Node.call(Unsafe.FUNCTION, get, Node.env(
						Node.call(set, Node.call(Unsafe.FUNCTION, root, Node.env(block), Node.call(Unsafe.FUNCTION, param, Node.env(
							Node.call(Unsafe.SCOPE, Node.call(Unsafe.COMPARE, Node.id("root"), param, Node.env(
								param
							), Node.env(
								isRoot
								? Node.call(get, Node.id(), Node.call(get, Node.id(), Unsafe.ROOT, Node.call(proot, param)))
								: Node.call(get, Node.id(), Unsafe.ROOT, Node.call(proot, param))
							)))
						))))
					))
				)))
			));
		}else {
			block = Node.call(Unsafe.FUNCTION, root, Node.env(block));
		}
		
		block = Node.call(Unsafe.FUNCTION, parent, Node.env(block));
		
		return block;
	}
	
	public ResolverVirtual insertRoot (Resolver r) {
		Pair[] thispairs = this.getPairs();
		
		for (int i = 0; i < thispairs.length; i++) {
			Pair pair = thispairs[i];
			
			if (pair.name.equals("root")) {
				Pair[] pairs = new Pair[thispairs.length];
				System.arraycopy(thispairs, 0, pairs, 0, thispairs.length);
				pairs[i] = new Pair(pair.name, ((ResolverVirtual) pair.resolver).insertRoot(r));
				
				return new ResolverVirtual(pairs);
			}
		}
		
		Pair[] pairs = new Pair[thispairs.length + 1];
		System.arraycopy(thispairs, 0, pairs, 0, thispairs.length);
		pairs[thispairs.length] = new Pair ("root", r);
		return new ResolverVirtual(pairs);
	}
	
	@Override
	public String toString() {
		Pair[] children = this.getPairs();
		
		if (children.length == 0) return "";
		
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < children.length; i++) {
			Pair entry = children[i];
			
			boolean last = i == children.length - 1;
			
			if (last) b.append('\u2514');
			else b.append('\u251C');
			
			if (!(entry.resolver instanceof ResolverSource)) {
				b.append(' ').append(entry.name).append('\n');
				b.append(last ? "  " : "\u2502 ");
				
				String val = entry.resolver.toString();
				
				for (int ii = 0; ii < val.length(); ii++) {
					char c = val.charAt(ii);
					
					if (c == '\n') {
						b.append(last ? "\n  " : "\n\u2502 ");
					}else {
						b.append(c);
					}
				}
				
				b.append("\n");
			}else{
				b.append(' ').append(Color.cyan(entry.name)).append('\n');
			}
		}
		
		return b.substring(0, b.length() - 1);
	}
	
	public static class Pair {
		public final String name;
		public final Resolver resolver;
		
		public Pair (String name, Resolver resolver) {
			this.name = name;
			this.resolver = resolver;
		}
		
		public Pair (String name, Node v) {
			this.name = name;
			this.resolver = new ResolverSource(v);
		}
	}
	
	private static class IdentifierPair extends Pair {
		public final NodeIdentifier uniqueIdentifier = Node.id();
		public final NodeIdentifier identifier;
		
		public IdentifierPair(Pair p) {
			super(p.name, p.resolver);
			
			this.identifier = Node.id(p.name);
		}
	}
}
