package resolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import parser.Color;
import value.Unsafe;
import value.node.Node;

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
		Pair root;
		Pair[] pairs;
		
		{
			HashSet<String> names = new HashSet<>();
			Pair[] p = this.getPairs();
			
			pairs = new Pair[p.length];
			int len = 0;
			
			for (int i = 0; i < p.length; i++) {
				String name = p[i].name;
				
				if (!names.contains(name)) {
					names.add(name);
					
					//if (name.equals("root")) {
					//	root = p[i];
					//} else {
						pairs[len++] = p[i];
					//}
				}
			}
			
			pairs = Arrays.copyOf(pairs, len);
		}
		
		Node rel = Node.id("[rel]");
		Node block = Node.call("[root]", rel);
		
		for (Pair p : pairs) {
			block = Node.call(Unsafe.SCOPE, Node.call("(compare)", rel, Node.id(p.name), Node.env(Node.id("[" + p.name + "]")), Node.env(block)));
		}
		
		block = Node.call("(function)", rel, Node.env(block));
		
		for (Pair p : pairs) {
			block = Node.call("(function)", Node.id("[" + p.name + "]"), Node.env(block), p.resolver.createNode());
		}
		
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
}
