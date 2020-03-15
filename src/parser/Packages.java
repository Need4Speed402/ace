package parser;

import java.io.File;

import parser.token.Token;
import parser.token.resolver.ResolverFile;
import parser.token.resolver.ResolverSource;
import parser.token.resolver.ResolverVirtual;
import value.Unsafe;
import value.node.Node;

public class Packages {
	public static final boolean RUNTIME_STATS = true;
	
	public static final boolean PRINT_AST = true;
	public static final boolean PRINT_EVENTS = false;
	
	public static long AST_TIME = 0;
	public static long NODE_TIME = 0;
	public static long RUN_TIME = 0;
	
	public static String formatTime (long time) {
		String out = Long.toString(time / 1000000L);
		
		for (int i = out.length() - 3; i > 0; i -= 3) {
			out = out.substring(0, i) + ',' + out.substring(i);
		}
		
		return out + " ms";
	}
	
	public static void main(String[] args) {
		File start = new File(args[0]);
		
		String name = start.getName();
		
		if (name.indexOf('.') >= 0) {
			name = name.substring(0, name.lastIndexOf('.'));
		}
		
		Token r = new ResolverVirtual ("root",
			Unsafe.createUnsafe(),
			new ResolverFile("std", new File("D:\\documents\\eclipse\\SimpleAceInterpreter\\src\\ace")).insert(new ResolverSource("root", Node.call(Node.id("unsafe"), Node.id("root")))),
			new ResolverFile("import", start.getParentFile()).insert(new ResolverSource("root", Node.call(Node.id("std"), Node.id("root"))))
		);
		
		//System.out.println(r);
		
		Node n = Node.call(r.createNode(), Unsafe.IDENTITY, Node.id("import"), Node.id(name), Node.id("`"));
		
		n.run(Unsafe.DEFAULT_ENVIRONMENT);
		
		/*Packages.file(args[0]);
		
		RUN_TIME += System.nanoTime() - start;
		
		if (RUNTIME_STATS) {
			System.out.println(Color.white(Color.bgBlack(" - Runtime Statistics - ")));
			System.out.println("AST / Parsing: " + formatTime(AST_TIME));
			System.out.println("NODE / Tree Generation: " + formatTime(NODE_TIME));
			System.out.println("Execution: " + formatTime(RUN_TIME - AST_TIME - NODE_TIME));
			System.out.println("Total time: " + formatTime(RUN_TIME));
		}*/
	}
}
