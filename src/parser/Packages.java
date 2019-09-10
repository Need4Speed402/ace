package parser;

import java.io.File;
import java.nio.file.Files;

import event.Event;
import event.EventScope;
import parser.token.Token;
import parser.token.TokenBase;
import value.Value;

public class Packages {
	public static Value load (Stream s, String name) {
		Token ast;
		
		try {
			ast = new TokenBase(s);
		}catch (ParserException e) {
			System.out.println(name + ":" + (s.getLine() + 1) + ":" + (s.getCol() + 1) + ": " + e.getMessage());
			
			throw e;
		}
		
		//System.out.println(ast.createEvent());
		
		Event event = new EventScope(ast.createEvent());
		event.init();
		event.paramaterHeight(new Node<Integer>(0), new Node<Integer>(0));
		Value v = event.run(Global.global);
		return v;
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
