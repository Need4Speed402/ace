package parser.resolver;

import java.io.InputStream;
import java.util.HashMap;

import parser.Packages;
import parser.Stream;
import value.Value;
import value.ValueIdentifier;

public class ResolverPackage implements Resolver{
	private final String path;
	
	private static HashMap<String, Value> cache = new HashMap<String, Value>();
	
	public ResolverPackage (String path) {
		this.path = path;
	}
	
	@Override
	public Value call(Value value) {
		if (!(value instanceof ValueIdentifier)) return Value.NULL;
		String name = ((ValueIdentifier) value).name;
		
		if (cache.containsKey(this.path + "/" + name)) {
			return cache.get(this.path + "/" + name);
		}else {
			InputStream stream = ResolverPackage.class.getClassLoader().getResourceAsStream(this.path + "/" + name + ".ace");
			
			Value resolution = stream == null ? new ResolverPackage(this.path + "/" + name) : new Value() {
				private Value val;
				
				@Override
				public Value call(Value v) {
					if (val == null) {
						System.out.println("Loading: " + name);
						
						val = Packages.load(new Stream(stream), new ResolverCompound(
							new ResolverScope(),
							new ResolverPath (new ResolverUnsafe(), "unsafe"),
							new ResolverPackage ("ace")
						), name);
					}
					
					return val.call(v);
				}
				
				@Override
				public String toString() {
					return super.toString() + "[" + path + "/" + name + "]";
				}
			};
			
			cache.put(this.path + "/" + name, resolution);
			return resolution;
		}
	}

	@Override
	public String toString() {
		return super.toString() + "[" + this.path + "]";
	}
}
