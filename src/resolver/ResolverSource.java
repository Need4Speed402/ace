package resolver;

import java.io.File;
import java.io.FileInputStream;

import parser.Packages;
import parser.Stream;
import parser.token.Token;
import parser.token.TokenBase;
import value.Unsafe;
import value.node.Node;

public class ResolverSource extends Resolver{
	private final Source source;
	
	public ResolverSource (File f) {
		this.source = () -> load(f);
	}
	
	public ResolverSource(String ... path) {
		this (pathAsNode(path));
	}
	
	public ResolverSource(Node node) {
		this.source = () -> node;
	}
	
	@Override
	public Node createNode() {
		/*
		 * Modules in ACE are loaded once exactly when they are needed and then the result
		 * of their execution is cached so when the module is used next time, that cached
		 * version will be used.
		 * 
		 * (function) memory {
		 *     (function) value {
		 *         (check) (memory get) uninitialized
		 *     }
		 * } ((memory) uninitialized)
		 */
		
		return Node.id("{source}");
		
		/*return Node.call("(function)", Node.call("memory"), Node.env(
			Node.call("(function)", Node.id("param"), Node.env(
				Node.call("memory", Node.id("memory"), Node.id("param"))
			))
		), Node.call("(memory)", Node.call("(function)", Node.id("memory"), Node.env(
			Node.call("(function)", Node.id("param"), Node.env(
				null
			))
		))));*/
	}

	private interface Source {
		public Node get ();
	}
	
	private static Node pathAsNode (String ... path) {
		Node nodes = Node.id(path[0]);
		
		for (int i = 1; i < path.length; i++) {
			nodes = Node.call(nodes, Node.id(path[i]));
		}
		
		return nodes;
	}
	
	public static Node load (File f) {
		Stream s;
		long p1 = System.nanoTime();
		
		try {
			s = new Stream(new FileInputStream(f));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		try {
			Token ast = new TokenBase(s);
			long p2 = System.nanoTime();
			Packages.AST_TIME += p2 - p1;
			
			Node node = ast.createNode();
			Packages.NODE_TIME += System.nanoTime() - p2;
			
			return node;
		}catch (Exception e) {
			System.out.println(f.getAbsolutePath() + ":" + (s.getLine() + 1) + ":" + (s.getCol() + 1) + ": " + e.getMessage());
			
			throw e;
		}
	}
}
