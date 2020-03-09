package resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import parser.Stream;
import parser.token.TokenStatement;

public class ResolverFile extends ResolverVirtual{
	private File root;
	
	public ResolverFile(File root, Pair ... pairs) {
		super(pairs);
		this.root = root;
	}
	
	public ResolverFile(File root) {
		this.root = root;
	}
	
	@Override
	public Pair[] getPairs () {
		ArrayList<Pair> pairs = new ArrayList<>();
		pairs.addAll(Arrays.asList(super.getPairs()));
		
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
				
				pairs.add(new Pair(name, new ResolverSource(file)));
			}
		}
		
		for (File file : files) {
			if (file.isDirectory()) {
				pairs.add(new Pair(file.getName(), new ResolverFile(file)));
			}
		}
		
		return pairs.toArray(new Pair[0]);
	}
}
