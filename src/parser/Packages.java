package parser;

import java.io.File;

import parser.token.Token;
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
		
		//the entire thing has to be wrapped in a root folder
		//so that unsafe, std, and import will be visible in the
		//default environment without any special logic elsewhere in code.
		Token r = new ResolverVirtual ("root",
			//new Pair("unsafe", Unsafe.createUnsafe()),
			//new Pair("std", new ResolverFile(new File("D:\\documents\\eclipse\\SimpleAceInterpreter\\src\\ace")).insertRoot(new ResolverSource("unsafe", "root"))),
			//new Pair("import", new ResolverFile(start.getParentFile()))
				
			new ResolverVirtual("poop", new ResolverSource("random", Node.id("Random"))),
				
			new ResolverSource("print", Node.call(Unsafe.DO, Node.call(Unsafe.CONSOLE, Node.call(Node.id("root"))))),
			
			new ResolverVirtual("root",
			//	new ResolverVirtual("root",
					new ResolverVirtual("dude",
						new ResolverSource("what", Node.call(Node.id("print"), Node.id("thestnh")))
					),
					
					new ResolverSource("hello", Node.call(Unsafe.DO, Node.call(Node.id("dude"), Node.id("what"), Node.id("hello world"))))
			//	)
			),
				
			new ResolverVirtual("dude",
				new ResolverSource("what", Node.call(Node.id("hello"), Node.id("aoesuthoeusnth")))
			),
				
			new ResolverSource(name, Node.call(Node.id("dude"), Node.id("what"), Node.id("hello world")))
		);
		
		System.out.println(r);
		
		Node n = Node.call(r.createNode(), Unsafe.IDENTITY, Unsafe.IDENTITY, Node.id(name), Node.id("`"));
		
		//System.out.println(n);
		n.run(Unsafe.DEFAULT_ENVIRONMENT);
		
		//System.out.println(n.toString());
		
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
