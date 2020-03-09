package parser;

import java.io.File;

import resolver.Resolver;
import resolver.ResolverFile;
import resolver.ResolverSource;
import resolver.ResolverVirtual;
import resolver.ResolverVirtual.Pair;
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
		Resolver r = new ResolverVirtual(
			new Pair("unsafe", Unsafe.createUnsafe()),
			new Pair("std", new ResolverFile(new File("D:\\documents\\eclipse\\SimpleAceInterpreter\\src\\ace")).insertRoot(new ResolverSource("unsafe", "root"))),
			new Pair("import", new ResolverFile(new File(args[0]).getParentFile()).insertRoot(new ResolverSource("std", "root")))
		);
		
		Node n = r.createNode();
		
		System.out.println(n.toString());
		
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
