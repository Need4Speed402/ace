package parser;

import java.io.File;
import java.nio.file.Files;

import node.Node;
import parser.resolver.Resolver;
import parser.resolver.ResolverCompound;
import parser.resolver.ResolverFile;
import parser.resolver.ResolverPath;
import parser.resolver.ResolverUnsafe;
import parser.token.Token;
import parser.token.TokenBase;
import value.Value;

public class Packages {
	public static final boolean RUNTIME_STATS = true;
	
	public static final boolean PRINT_AST = false;
	public static final boolean PRINT_EVENTS = false;
	
	public static File root;
	
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
		long start = System.nanoTime();
		
		root = new File(args[0]).getParentFile();
		Packages.file(args[0]);
		
		RUN_TIME += System.nanoTime() - start;
		
		if (RUNTIME_STATS) {
			System.out.println(" - Runtime Statistics - ");
			System.out.println("AST / Parsing: " + formatTime(AST_TIME));
			System.out.println("NODE / Tree Generation: " + formatTime(NODE_TIME));
			System.out.println("Execution: " + formatTime(RUN_TIME - AST_TIME - NODE_TIME));
			System.out.println("Total time: " + formatTime(RUN_TIME));
		}
	}
	
	public static Value load (Stream s, Resolver resolver, String name) {
		Token ast;
		
		long startAST = System.nanoTime();
		
		try {
			//when trying to run an .ace file as a bash file, the bash interpreter
			//will look for a string starting with #! to signify what program to use to run this file
			//this lets you run a .ace file as a regular bash script as long as it starts with '#!'
			//followed by the path to the compiler/interpreter
			if (s.isNext("#!")) while (s.hasChr() && !s.next('\n')) s.chr();
			
			ast = new TokenBase(s);
		}catch (Exception e) {
			System.out.println(name + ":" + (s.getLine() + 1) + ":" + (s.getCol() + 1) + ": " + e.getMessage());
			
			throw e;
		}
		
		AST_TIME += System.nanoTime() - startAST;
		
		if (PRINT_AST) {
			System.out.println(ast);
			
			return null;
		}else {
			long startNode = System.nanoTime();
			Node node = ast.createNode();
			NODE_TIME += System.nanoTime() - startNode;
			
			if (PRINT_EVENTS) {
				System.out.println(node);
				return null;
			}else {
				return node.run(Resolver.createNode(resolver));
			}
		}
	}
	
	public static Value file (String path) {
		try {
			return Packages.load(new Stream(Files.readAllBytes(new File(path).toPath())), new ResolverCompound(
				new ResolverFile(Packages.root),
				//new ResolverPackage("ace"),
				new ResolverPath(new ResolverFile(new File("D:\\documents\\eclipse\\SimpleAceInterpreter\\src\\ace")), "std"),
				new ResolverPath(new ResolverUnsafe(), "unsafe")
			), path);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
