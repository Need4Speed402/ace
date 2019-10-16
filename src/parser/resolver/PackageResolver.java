package parser.resolver;

import java.io.InputStream;
import java.util.HashMap;

import parser.Packages;
import parser.Stream;
import value.Value;
import value.ValueIdentifier;

public class PackageResolver implements Resolver{
	private final String path;
	
	private static HashMap<String, Value> cache = new HashMap<String, Value>();
	
	public PackageResolver (String path) {
		this.path = path;
	}
	
	@Override
	public Value call(Value value) {
		if (!(value instanceof ValueIdentifier)) return Value.NULL;
		String name = ((ValueIdentifier) value).id;
		
		if (cache.containsKey(this.path + "/" + name)) {
			return cache.get(this.path + "/" + name);
		}else {
			InputStream stream = PackageResolver.class.getClassLoader().getResourceAsStream(this.path + "/" + name + ".ace");
			
			Value resolution = stream == null ? new PackageResolver(this.path + "/" + name) : new Value() {
				private Value val;
				
				@Override
				public Value call(Value v) {
					if (val == null) {
						System.out.println("Loading: " + name);
						
						val = Packages.load(new Stream(stream), new CompoundResolver(
							new PathResolver (new UnsafeResolver(), "unsafe"),
							new PackageResolver ("ace")
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
