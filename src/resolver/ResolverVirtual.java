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
	public Node createNode() {
		IdentifierPair root = null;
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
					if (name.equals("root")) root = pairs[len - 1];
				}
			}
			
			pairs = Arrays.copyOf(pairs, len);
		}
		
		Node rel = Node.id();
		Node block = rel;
		
		for (IdentifierPair p : pairs) {
			block = Node.call(Unsafe.SCOPE, Node.call(Unsafe.COMPARE, rel, Node.id(p.name), Node.env(p.identifier), Node.env(block)));
		}
		
		block = Node.call(Unsafe.FUNCTION, rel, Node.env(block));
		
		for (IdentifierPair p : pairs) {
			block = Node.call(Unsafe.FUNCTION, p.identifier, Node.env(block), Node.call(Unsafe.ASSIGN, Node.id(p.name), p.resolver.createNode()));
		}
		
		/*if (root != null) {
			Node set = Node.id();
			Node get = Node.id();
			Node param = Node.id();
			
			block = Node.call(Unsafe.MUTABLE, Unsafe.DO, Node.call(Unsafe.FUNCTION, set, Node.env(
				Node.call(Unsafe.FUNCTION, get, Node.env(
					Node.call(set, Node.call(Node.env(block), Node.call(Unsafe.FUNCTION, param, Node.env(
						Node.call(get, Node.id(), Node.id("root"), param)	
					))))
				))
			)));
		}*/
		
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
		public NodeIdentifier identifier = Node.id();
		
		public IdentifierPair(Pair p) {
			super(p.name, p.resolver);
		}
	}
}
