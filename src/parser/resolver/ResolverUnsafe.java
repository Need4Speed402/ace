package parser.resolver;

import java.util.HashMap;

import value.Value;
import value.ValueIdentifier;

public class ResolverUnsafe implements Resolver{
	private static HashMap<String, Value> cache = new HashMap<>();
	private final String path;
	
	public ResolverUnsafe (String path) {
		this.path = path;
	}
	
	public ResolverUnsafe () {
		this("");
	}
	
	@Override
	public Value call(Value p1) {
		if (p1 instanceof ValueIdentifier) {
			String p = this.path + ((ValueIdentifier) p1).name;
			
			if (cache.containsKey(p)) {
				return cache.get(p);
			}else {
				Value v = null;
				
				if (ResolverUnsafe.class.getClassLoader().getResource("unsafe/" + p + ".class") != null) {
					System.out.println("Loading unsafe: " + p);
					
					try{
						v = (Value) Class.forName("unsafe." + p.replaceAll("/", ".")).newInstance();
					}catch (Exception e) {}
				}
				
				if (v == null) v = new ResolverUnsafe(p + "/");
				
				cache.put(p, v);
				return v;
			}
		}
		
		return Value.NULL;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + this.path + "]";
	}
}
