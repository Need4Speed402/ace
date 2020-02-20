package value.resolver;

import java.util.Arrays;

import value.Value;

public class ResolverCompound extends Resolver{
	private final Resolver parent, resolver;
	
	public ResolverCompound (Resolver ... resolvers) {
		this.resolver = resolvers[0];
		
		if (resolvers.length == 1) {
			this.parent = null;
		}else {
			this.parent = new ResolverCompound(Arrays.copyOfRange(resolvers, 1, resolvers.length));
		}
	}
	
	@Override
	public Value exists(Resolver parent, String[] path) {
		Value v = this.resolver.exists(this, path);
		
		if (v == null && this.parent != null) {
			v = this.parent.exists(parent, path);
		}
		
		return v;
	}
}
