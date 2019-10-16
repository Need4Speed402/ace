package parser.resolver;

import java.util.Arrays;

import value.Value;
import value.ValueIdentifier;

public class PathResolver implements Resolver{
	private final Resolver child;
	private final String[] path;
	private final int offset;
	
	public PathResolver (Resolver child, String path) {
		this(child, path.split("/"));
	}
	
	public PathResolver (Resolver child, String ... path) {
		this(child, 0, path);
	}

	public PathResolver (Resolver child, int offset, String ... path) {
		this.child = child;
		this.path = path;
		this.offset = offset;
	}
	
	@Override
	public Value call(Value value) {
		if (!(value instanceof ValueIdentifier)) return Value.NULL;
		String name = ((ValueIdentifier) value).id;
		
		if (name.equals(this.path[this.offset])) {
			if (this.offset + 1 == this.path.length) {
				return this.child;
			}else {
				return new PathResolver (this.child, this.offset + 1, this.path);
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
