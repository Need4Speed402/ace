package parser.resolver;

import java.util.Arrays;

import value.Value;
import value.ValueIdentifier;

public class ResolverPath implements Resolver{
	private final Resolver child;
	private final String[] path;
	private final int offset;
	
	public ResolverPath (Resolver child, String path) {
		this(child, path.split("/"));
	}
	
	public ResolverPath (Resolver child, String ... path) {
		this(child, 0, path);
	}

	public ResolverPath (Resolver child, int offset, String ... path) {
		this.child = child;
		this.path = path;
		this.offset = offset;
	}
	
	@Override
	public Value call(Value value) {
		if (!(value instanceof ValueIdentifier)) return Value.NULL;
		String name = ((ValueIdentifier) value).name;
		
		if (name.equals(this.path[this.offset])) {
			if (this.offset + 1 == this.path.length) {
				return this.child;
			}else {
				return new ResolverPath (this.child, this.offset + 1, this.path);
			}
		}else {
			return Resolver.NULL;
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + Arrays.toString(Arrays.copyOfRange(this.path, 0, this.offset + 1));
	}
	
}
