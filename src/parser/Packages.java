package parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import parser.token.Token;
import parser.token.resolver.Directory;
import parser.token.resolver.EntrySource;
import parser.token.resolver.Source;
import parser.token.resolver.Unsafe;
import parser.token.resolver.Virtual;
import test.Test;
import value.Value;
import value.effect.Runtime;
import value.intrinsic.Environment;
import value.node.Node;

public class Packages {
	public static final boolean RUNTIME_STATS = true;
	
	public static final boolean PRINT_AST = true;
	public static final boolean PRINT_EVENTS = false;
	
	public static long AST_TIME = 0;
	public static long NODE_TIME = 0;
	public static long RUN_TIME = 0;
	public static long RESOLVE_TIME = 0;
	
	public static String formatTime (long time) {
		String error = Double.toString((time % 1000000L) / 1000000.0);
		
		String out = Long.toString(time / 1000000L);
		
		for (int i = out.length() - 3; i > 0; i -= 3) {
			out = out.substring(0, i) + ',' + out.substring(i);
		}
		
		return out + error.substring(1, Math.min(error.length(), 4)) + " ms";
	}
	
	public static void main(String[] args) throws IOException{
		if (args.length == 0) {
			System.out.println("No input");
			return;
		}else if (args.length == 1) {
			args = new String[] {"run", args[0]};
		}
		
		//args = new String[] {"run", "/home/alex/Documents/ace/main.ace"};
		
		String directive = args[0];
		
		if (directive.equals("run")) {
			File entry = new File(args[1]);
			
			String name = entry.getName();
			
			if (name.endsWith(".ace")) {
				name = name.substring(0, name.lastIndexOf('.'));
			}else {
				throw new RuntimeException("Program entry not ace source code");
			}
			
			Token r = new Virtual ("root",
				Unsafe.instance,
				//new Directory("std", new File("/media/wdhhd/documents/eclipse/SimpleAceInterpreter/src/ace"))
				//	.insert(new Source("root", Node.call(Node.id("unsafe"), Node.id("root")))),
				new Directory("import", entry.getParentFile())
					.insert(new Source("root", Node.call(Node.id("unsafe"), Node.id("root"))))
					.set(new EntrySource(name, entry))
			);
			
			long start = System.nanoTime();
			Node program = Node.call(r.createNode(), Unsafe.IDENTITY, Node.id("import"), Node.id(name), Node.id("`"));
			NODE_TIME += System.nanoTime() - start;
			
			Value v = Environment.exec(program);
			
			RESOLVE_TIME += System.nanoTime() - start;
			
			new Runtime().run(v);
			
			RUN_TIME += System.nanoTime() - start;
			
			if (RUNTIME_STATS) {
				System.out.println(Color.white(Color.bgBlack(" - Runtime Statistics - ")));
				System.out.println("AST / Parsing: " + formatTime(AST_TIME));
				System.out.println("NODE / Tree Generation: " + formatTime(NODE_TIME));
				System.out.println("Resolving: " + formatTime(RESOLVE_TIME));
				System.out.println("Execution: " + formatTime(RUN_TIME - AST_TIME - NODE_TIME - RESOLVE_TIME));
				System.out.println("Total time: " + formatTime(RUN_TIME));
			}
		}else if (directive.equals("test")) {
			Test.test(new File(args[1]));
		}
	}
}
