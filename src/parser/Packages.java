package parser;

import java.io.File;
import java.nio.file.Files;

import node.Node;
import node.NodeParameter;
import parser.token.Token;
import parser.token.TokenScope;
import value.Value;

public class Packages {
	public static final boolean PRINT_AST = false;
	public static final boolean PRINT_EVENTS = true;
	
	public static Value load (Stream s, String name) {
		Token ast;
		
		try {
			ast = TokenScope.createBase(s);
		}catch (ParserException e) {
			System.out.println(name + ":" + (s.getLine() + 1) + ":" + (s.getCol() + 1) + ": " + e.getMessage());
			
			throw e;
		}
		
		if (PRINT_AST) {
			System.out.println(ast);
			
			return null;
		}else {
			Node event = ast.createEvent();
			event.init();
			event.paramaterHeight(NodeParameter.createNodeList());
			
			if (PRINT_EVENTS) {
				System.out.println(event);
				return null;
			}else {
				return event.run(Global.global);
			}
		}
	}
	
	public static Value file (String path) {
		try {
			return Packages.load(new Stream(Files.readAllBytes(new File(path).toPath())), path);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Value getPackage (String name) {
		try {
			return Packages.load(new Stream(Packages.class.getClassLoader().getResourceAsStream("ace/" + name)), name);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
