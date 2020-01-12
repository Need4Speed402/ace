package parser.resolver;

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
	public void setParent(Resolver parent) {
		super.setParent(parent);
		
		this.child.setParent(parent);
	}
	
	@Override
	public Value exists(String[] path) {
		if (path.length > this.path.length) {
			for (int i = 0; i < this.path.length; i++) {
				if (!this.path[i].equals(path[i])) return null;
			}
		}
		
		return this.child.exists(Arrays.copyOfRange(path, this.path.length, path.length));
	}
	
	@Override
	public String toString() {
		return super.toString() + Arrays.toString(this.path);
	}
	
}
