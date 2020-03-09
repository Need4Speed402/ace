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
		this.source = () -> Node.call("console", Node.id("hello world"));//load(f);
	}
	
	public ResolverSource(String ... path) {
		this (pathAsNode(path));
	}
	
	public ResolverSource(Node node) {
		this.source = () -> node;
	}
	
	@Override
	public Node createNode() {
		return Node.call("(mutable)", Node.id("[void]"), Node.call("(function)", Node.id("[set]"), Node.env(
			Node.call("(function)", Node.id("[get]"), Node.env(
				Node.call(Unsafe.DO, Node.call("[set]", Node.call("(function)", Node.id("[param]"), Node.env(
					Node.call(Unsafe.DO,
						Node.call("[set]", Node.id("(parent)")),
						Node.call("[set]", this.source.get(), Node.id("[param]"))
					)
				))), Node.call("(function)", Node.id("[param]"), Node.env(Node.call("[get]", Node.id("[void]"), Node.id("[param]")))))
			))
		)));
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
