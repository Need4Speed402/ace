package parser.token.resolver;

import java.io.File;
import java.io.FileInputStream;

import parser.Color;
import parser.Packages;
import parser.Stream;
import parser.token.Resolver;
import parser.token.Token;
import parser.token.syntax.TokenBase;
import value.node.Node;

public class Source extends Resolver{
	private final Node source;
	
	public Source (String name, File f) {
		this(name, Node.delegate(() -> {
			System.out.println("Loading: " + f);
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
		}));
	}
	
	public Source(String name, Node node) {
		super(name);
		if (node == null) throw new NullPointerException();
		
		this.source = node;
	}
	
	public Node getSource() {
		return this.source;
	}
	
	@Override
	public Node createNode() {
		Node set = Node.id();
		Node get = Node.id();
		Node param = Node.id();
		Node root = Node.id();
		
		return Node.call(Unsafe.FUNCTION, root, Node.env(
			Node.call(Unsafe.MUTABLE, Node.id(), Node.call(Unsafe.FUNCTION, set, Node.env(
				Node.call(Unsafe.FUNCTION, get, Node.env(
					Node.call(Unsafe.DO, Node.call(set, Node.call(Unsafe.FUNCTION, param, Node.env(
						Node.call(Unsafe.DO,
							Node.call(Unsafe.DO,
								Node.call(set, Node.call(root, Unsafe.PARENT, Node.id(this.getName()))),
								Node.call(set, Node.call(Node.env(this.source), root))
							),
							Node.call(get, Node.id(), param)
						)
					))), Node.call(Unsafe.FUNCTION, param, Node.env(Node.call(get, Node.id(), param))))
				))
			)))
		));
	}

	@Override
	public String toString() {
		return Color.cyan(this.getName());
	}
}
