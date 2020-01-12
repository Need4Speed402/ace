package parser.resolver;

import java.util.HashMap;

import value.Value;

public class ResolverUnsafe extends Resolver{
	private static HashMap<String, Value> cache = new HashMap<>();
	
	@Override
	public Value exists(String[] path) {
		if (path.length == 1) {
			path = new String[] {"unsafe", "lang", path[0]};
		}
		
		String joinedPath = String.join(".", path);
		Value resolution = null;
		
		if (!cache.containsKey(joinedPath)) {
			//System.out.println("Loading: " + joinedPath);
			
			try{
				resolution = (Value) Class.forName(joinedPath).newInstance();
			}catch (Exception e) {}
			
			cache.put(joinedPath, resolution);
		}else {
			resolution = cache.get(joinedPath);
		}
		
		return resolution;
	}
}
