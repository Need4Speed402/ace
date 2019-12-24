package parser.resolver;

import java.io.InputStream;
import java.util.HashMap;

import parser.Packages;
import parser.Stream;
import value.Value;

public class ResolverPackage implements Resolver{
	private final String root;
	
	private static HashMap<String, Value> cache = new HashMap<String, Value>();
	
	public ResolverPackage (String root) {
		this.root = root;
	}
	
	@Override
	public Value exists(String[] path) {
		String joinPath = this.root + "/" + String.join("/", path);
		
		Value resolution = cache.get(joinPath);
		
		if (resolution == null) {
			InputStream stream = ResolverPackage.class.getClassLoader().getResourceAsStream(joinPath + ".ace");
			
			if (stream != null) {
				return new Value() {
					private Value val;
					
					@Override
					public Value call(Value v) {
						if (val == null) {
							System.out.println("Loading: " + joinPath);
							
							val = Packages.load(new Stream(stream), new ResolverCompound(
								new ResolverUnsafe(),
								new ResolverPackage ("ace")
							), joinPath);
						}
						
						return val.call(v);
					}
					
					@Override
					public String toString() {
						return super.toString() + "[" + joinPath + "]";
					}
				};
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + this.root + "]";
	}
}
