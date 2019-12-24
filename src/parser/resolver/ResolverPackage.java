package parser.resolver;

import java.io.InputStream;
import java.util.HashMap;

import parser.Packages;
import parser.Stream;
import value.Value;

public class ResolverPackage implements Resolver{
	private final String root;
	
	private static HashMap<String, Cache> cache = new HashMap<String, Cache>();
	
	public ResolverPackage (String root) {
		this.root = root;
	}
	
	@Override
	public Value exists(String[] path) {
		String joinPath = this.root + "/" + String.join("/", path);
		
		Cache resolution = cache.get(joinPath);
		
		if (resolution == null) {
			InputStream stream = ResolverPackage.class.getClassLoader().getResourceAsStream(joinPath + ".ace");
			
			if (stream != null) {
				resolution = new Cache(joinPath, new Stream(stream));
			}
			
			cache.put(joinPath, resolution);
		}else if (resolution.running) {
			return null;
		}
		
		return resolution;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + this.root + "]";
	}
	
	public static class Cache implements Value{
		private Value val;
		
		private final String path;
		private final Stream stream;
		private final Resolver resolver;
		
		public boolean running;
		
		public Cache (String path, Stream stream, Resolver ... resolvers) {
			this.path = path;
			this.stream = stream;
			
			if (resolvers.length == 1) {
				this.resolver = resolvers[0];
			}else if (resolvers.length == 0) {
				this.resolver = new ResolverCompound(
					new ResolverPackage ("ace"),
					new ResolverUnsafe()
				);
			}else {
				this.resolver = new ResolverCompound(resolvers);
			}
		}
		
		@Override
		public Value call(Value v) {
			this.running = true;
			
			if (this.val == null) {
				System.out.println("Loading: " + this.path);
				
				this.val = Packages.load(this.stream, this.resolver, this.path);
			}
			
			
			Value ret = this.val.call(v);
			this.running = false;
			
			return ret;
		}
		
		@Override
		public String toString() {
			return super.toString() + "[" + this.path + "]";
		}
	}
}
