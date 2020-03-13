package parser.token.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import parser.Stream;
import parser.token.Resolver;
import parser.token.syntax.TokenStatement;

public class ResolverFile extends ResolverVirtual{
	private File root;
	
	public ResolverFile(String name, File root, Resolver ... resolvers) {
		super(name, resolvers);
		this.root = root;
	}
	
	public ResolverFile(String name, File root) {
		super(name);
		this.root = root;
	}
	
	@Override
	public Resolver[] getResolvers () {
		ArrayList<Resolver> pairs = new ArrayList<>();
		pairs.addAll(Arrays.asList(super.getResolvers()));
		
		File[] files = this.root.listFiles();
		
		main:for (File file : files) {
			String name = file.getName();
			
			if (name.toLowerCase().endsWith(".ace") && file.isFile()) {
				name = name.substring(0, name.length() - 4);
				
				//skip all files that have names that can't be accessed through ace syntax.
				for (int i = 0; i < name.length(); i++) {
					char c = name.charAt(i);
					
					if ((i == 0 || i == name.length()) && TokenStatement.operators.indexOf(c) >= 0) {
						continue main;
					}else if (new String(Stream.whitespace).indexOf(c) >= 0 || "(){}[];\"'".indexOf(c) >= 0) {
						continue main;
					}
				}
				
				pairs.add(new ResolverSource(name, file));
			}
		}
		
		for (File file : files) {
			if (file.isDirectory()) {
				pairs.add(new ResolverFile(file.getName(), file));
			}
		}
		
		return pairs.toArray(new Resolver[0]);
	}
}
