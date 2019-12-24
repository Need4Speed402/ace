package parser.resolver;

import java.io.File;
import java.util.HashMap;

import parser.Packages;
import value.Value;

public class ResolverFile implements Resolver{
	private File root;
	private static HashMap<String, Value> resolutions = new HashMap<>();
	
	public ResolverFile(File root) {
		this.root = root;
	}
	
	@Override
	public Value exists(String[] path) {
		File file = new File(this.root, String.join("/", path) + ".ace");
		
		Value resolution = resolutions.get(file.getAbsolutePath());
		
		if (resolution == null && file.isFile()) {
			resolution = p -> Packages.file(file.getAbsolutePath() + ".ace").call(p);
			resolutions.put(file.getAbsolutePath(), resolution);
		}
		
		return resolution;
	}
}
