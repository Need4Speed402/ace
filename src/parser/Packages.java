package parser;

import java.io.File;
import java.nio.file.Files;

import event.Event;
import event.EventParameter;
import parser.token.Token;
import parser.token.TokenScope;
import value.Value;

public class Packages {
	public static final boolean PRINT_AST = false;
	
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
			Event event = ast.createEvent();
			event.init();
			event.paramaterHeight(EventParameter.createNodeList());
			return event.run(Global.global);
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
