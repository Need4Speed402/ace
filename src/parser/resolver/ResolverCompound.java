package parser.resolver;

import java.util.HashMap;
import java.util.LinkedList;

import value.Value;
import value.ValueIdentifier;

public class ResolverCompound implements Resolver{
	private Value[] resolvers;
	private HashMap<String, Value> indexes = new HashMap<>();
	
	public ResolverCompound (Value ... resolvers) {
		this.resolvers = resolvers;
	}
	
	@Override
	public Value call (Value value) {
		if (!(value instanceof ValueIdentifier)) return Value.NULL;
		String name = ((ValueIdentifier) value).name;
		
		if (this.indexes.containsKey(name)) {
			return this.indexes.get(name);
		}else {
			Value resolution = Resolver.NULL;
			
			for (int i = this.resolvers.length - 1; i >= 0; i--) {
				Value r = this.resolvers[i].call(value);
				
				if (r != Resolver.NULL) {
					if (r instanceof Resolver) {
						LinkedList<Value> resolvers = new LinkedList<>();
						resolvers.push(r);
						
						for (int ii = i - 1; ii >= 0; ii--) {
							Value rr = this.resolvers[ii].call(value);
							
							if (rr != Resolver.NULL) {
								if (rr instanceof Resolver) {
									resolvers.push(rr);
								}else {
									resolvers.clear();
									resolvers.push(rr);
									break;
								}
							}
						}
						
						if (resolvers.size() == 1) {
							resolution = resolvers.get(0);
						}else {
							resolution = new ResolverCompound(resolvers.toArray(new Resolver[0]));
						}
					}else {
						resolution = r;
					}
					
					break;
				}
			}
			
			this.indexes.put(name, resolution);
			return resolution;
		}
	}
}
