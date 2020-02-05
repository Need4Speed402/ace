package parser.resolver;

import java.util.Arrays;

import value.Value;

public class ResolverCompound extends Resolver{
	private Resolver[] resolvers;
	
	public ResolverCompound (Resolver ... resolvers) {
		this.resolvers = resolvers;
		
		ResolverCompound current = this;
		
		for (int i = 0; i < this.resolvers.length; i++) {
			this.resolvers[i].setParent(current);
			
			current = current.dropLevel();
		}
	}
	
	public ResolverCompound dropLevel () {
		if (this.resolvers.length == 1) return null;
		
		return new ResolverCompound(Arrays.copyOfRange(this.resolvers, 1, this.resolvers.length));
	}
	
	@Override
	public Value exists(String[] path) {
		for (int i = 0; i < this.resolvers.length; i++) {
			Value v = this.resolvers[i].exists(path);
			
			if (v != null) {
				return v;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + Arrays.toString(this.resolvers);
	}
}
