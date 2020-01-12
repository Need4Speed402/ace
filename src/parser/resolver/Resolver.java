package parser.resolver;

import value.Value;
import value.ValueIdentifier;

public abstract class Resolver {
	private Resolver parent;
	
	public abstract Value exists (String[] path);
	
	public Resolver getParent () {
		return this.parent;
	}
	
	public void setParent (Resolver parent) {
		this.parent = parent;
	}
	
	public static Value createNode (Resolver r) {
		return new ValueResolver(r);
	}
	
	public static class ValueResolver implements Value {
		private final Resolver resolver;
		private final String[] path;
		
		public ValueResolver (Resolver resolver, String ... path) {
			this.resolver = resolver;
			this.path = path;
		}
		
		@Override
		public Value call(Value v) {
			if (v instanceof ValueIdentifier) {
				String s = ((ValueIdentifier) v).name;
				
				String[] npath = new String[this.path.length + 1];
				System.arraycopy(this.path, 0, npath, 0, this.path.length);
				npath[this.path.length] = s;
				
				Value ret = this.resolver.exists(npath);
				
				if (ret == null) {
					ret = new ValueResolver(this.resolver, npath);
				}
				
				return ret;
			}
			
			return Value.NULL;
		}
	}
}
