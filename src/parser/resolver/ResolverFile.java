package parser.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import parser.Packages;
import parser.Stream;
import value.Value;

public class ResolverFile extends Resolver{
	private File root;
	private static HashMap<String, Value> cache = new HashMap<>();
	
	public ResolverFile(File root) {
		this.root = root;
	}
	
	@Override
	public Value exists(String[] path) {
		File file = new File(this.root, String.join("/", path) + ".ace");
		String str = file.getAbsolutePath();
		
		Value resolution = null;
		
		if (!cache.containsKey(str)) {
			cache.put(str, null);
			
			if (file.isFile()) try {
				resolution = Packages.load(new Stream(new FileInputStream(file)), getParent(), str);
				
				cache.put(str, resolution);
			}catch (IOException e) {}
		}else{
			resolution = cache.get(str);
		}
		
		return resolution;
	}
}
