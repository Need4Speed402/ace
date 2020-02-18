package value.resolver;

import java.io.InputStream;
import java.util.HashMap;

import parser.Packages;
import parser.Stream;
import value.Value;

public class ResolverPackage extends Resolver{
	private final String root;
	
	private static HashMap<String, Value> cache = new HashMap<>();
	
	public ResolverPackage (String root) {
		this.root = root;
	}
	
	@Override
	public Value exists(String[] path) {
		String joinPath = this.root + "/" + String.join("/", path);
		
		Value resolution = null;
		
		if (!cache.containsKey(joinPath)) {
			cache.put(joinPath, null);
			
			InputStream stream = ResolverPackage.class.getClassLoader().getResourceAsStream(joinPath + ".ace");
			
			if (stream != null) {
				resolution = Packages.load(new Stream(stream), this.getParent(), joinPath);
				cache.put(joinPath, resolution);
			}
		}else{
			resolution = cache.get(joinPath);
		}
		
		return resolution;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + this.root + "]";
	}
}
