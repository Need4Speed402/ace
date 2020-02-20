package value.resolver;

import java.util.Arrays;

import value.Value;

public class ResolverPath extends Resolver{
	private final Resolver child;
	private final String[] path;
	
	public ResolverPath (Resolver child, String path) {
		this(child, path.split("/"));
	}
	
	public ResolverPath (Resolver child, String ... path) {
		this.child = child;
		this.path = path;
	}
	
	@Override
	public Value exists(Resolver parent, String[] path) {
		if (path.length == 1) {
			Value res = this.child.exists(parent, new String[] {"root", path[0]});
			if (res != null) return res;
		}
		
		if (path.length > this.path.length) {
			for (int i = 0; i < this.path.length; i++) {
				if (this.path[i] != path[i]) return null;
			}
			
			return this.child.exists(parent, Arrays.copyOfRange(path, this.path.length, path.length));
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + Arrays.toString(this.path);
	}
	
}
