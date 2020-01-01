package parser.resolver;

import java.util.HashMap;

import value.Value;

public class ResolverUnsafe implements Resolver{
	private static HashMap<String, Value> cache = new HashMap<>();
	
	@Override
	public Value exists(String[] path) {
		if (path.length == 1) {
			path = new String[] {"unsafe", "lang", path[0]};
		}
		
		String joinedPath = String.join("/", path);
		Value resolution = cache.get(joinedPath);
		
		if (resolution == null && ResolverUnsafe.class.getClassLoader().getResource(joinedPath + ".class") != null) {
			System.out.println("Loading: " + joinedPath);
			
			try{
				resolution = (Value) Class.forName(String.join(".", path)).newInstance();
				cache.put(joinedPath, resolution);
			}catch (Exception e) {}
		}
		
		return resolution;
	}
}
