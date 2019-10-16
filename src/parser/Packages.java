package parser;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import node.Node;
import node.NodeIdentifier;
import node.NodeParameter;
import node.NodeScope;
import parser.resolver.CompoundResolver;
import parser.resolver.FileResolver;
import parser.resolver.PackageResolver;
import parser.resolver.PathResolver;
import parser.resolver.UnsafeResolver;
import parser.token.Token;
import parser.token.TokenScope;
import value.Value;
import value.ValueIdentifier;

public class Packages {
	public static final boolean PRINT_AST = false;
	public static final boolean PRINT_EVENTS = false;
	
	public static File root;
	
	public static void main(String[] args) {
		root = new File(args[0]).getParentFile();
		
		Packages.file(args[0]);
		
		System.out.println("\nmem: " + NodeScope.scopes + ":" + NodeScope.mem);
	}
	
	public static Value load (Stream s, Value resolver, String name) {
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
			
			List<NodeIdentifier> idents = new ArrayList<>();
			event.indexIdentifiers(null, idents);
			event.paramaterHeight(NodeParameter.createNodeList());
			
			ValueIdentifier[] identifiers = new ValueIdentifier[idents.size()];
			
			for (int i = 0; i < idents.size(); i++) {
				String ident = idents.get(i).name;
				
				identifiers[i] = new ValueIdentifier(ident, resolver.call(ident));
			}
			
			Local global = new Local(null, identifiers);
			event.init(global);
			
			if (PRINT_EVENTS) {
				System.out.println(event);
				return null;
			}else {
				return event.run(global, new LinkedNode<Value>(Value.NULL));
			}
		}
	}
	
	public static Value file (String path) {
		try {
			return Packages.load(new Stream(Files.readAllBytes(new File(path).toPath())), new CompoundResolver(
				new PathResolver (new UnsafeResolver(), "unsafe"),
				new PackageResolver("ace"),
				new FileResolver(Packages.root),
				new FileResolver(new File(path).getParentFile())
			), path);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
