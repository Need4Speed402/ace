package value.resolver;

import java.io.File;
import java.io.FileInputStream;

import parser.Packages;
import parser.Stream;
import parser.token.Token;
import parser.token.TokenBase;
import value.Value;
import value.node.Node;

public class ResolverSource extends Resolver{
	private final Source source;
	
	public ResolverSource (Value v) {
		this.source = parent -> v;
	}
	
	public ResolverSource (File f) {
		this (load(f));
	}
	
	public ResolverSource(String ... path) {
		this (pathAsNode(path));
	}
	
	public ResolverSource(Node node) {
		this.source = new Source () {
			private boolean isLoading = false;
			private Value value = null;
			
			@Override
			public Value get(Resolver parent) {
				if (this.isLoading) {
					// TODO
				}
				
				this.isLoading = true;
				//TODO
				this.value = node.run(env -> {
					return null;
				});
				this.isLoading = false;
				
				return this.value;
			}
		};
	}
	
	@Override
	public Value call(Resolver parent) {
		return v -> {
			return this.source.get(parent).call(v);
		};
	}

	private interface Source {
		public Value get (Resolver parent);
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
