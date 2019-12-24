package parser.resolver;

import java.util.HashMap;

import value.Value;

public class ResolverUnsafe implements Resolver{
	private static HashMap<String, Value> cache = new HashMap<>();
	
	@Override
	public Value exists(String[] path) {
		String joinedPath = String.join("/", path);
		Value resolution = cache.get(joinedPath);
		
		if (resolution == null && ResolverUnsafe.class.getClassLoader().getResource("unsafe/" + joinedPath + ".class") != null) {
			System.out.println("Loading unsafe: " + joinedPath);
			
			try{
				resolution = (Value) Class.forName("unsafe." + String.join(".", path)).newInstance();
				cache.put(joinedPath, resolution);
			}catch (Exception e) {}
		}
		
		return resolution;
	}
}
