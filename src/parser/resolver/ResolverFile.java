package parser.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import parser.Stream;
import value.Value;

public class ResolverFile extends Resolver{
	private File root;
	private static HashMap<String, ResolverPackage.Cache> resolutions = new HashMap<>();
	
	public ResolverFile(File root) {
		this.root = root;
	}
	
	@Override
	public Value exists(String[] path) {
		File file = new File(this.root, String.join("/", path) + ".ace");
		
		ResolverPackage.Cache resolution = resolutions.get(file.getAbsolutePath());
		
		if (resolution == null) {
			if (file.isFile()) try {
				resolution = new ResolverPackage.Cache(file.getAbsolutePath(), new Stream(new FileInputStream(file)), getParent());
			}catch (IOException e) {}
			
			resolutions.put(file.getAbsolutePath(), resolution);
		}else if (resolution.running) {
			return null;
		}
		
		return resolution;
	}
}
