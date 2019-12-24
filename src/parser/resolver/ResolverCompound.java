package parser.resolver;

import value.Value;

public class ResolverCompound implements Resolver{
	private Resolver[] resolvers;
	
	public ResolverCompound (Resolver ... resolvers) {
		this.resolvers = resolvers;
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
}
