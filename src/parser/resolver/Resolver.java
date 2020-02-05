package parser.resolver;

import value.Value;
import value.ValueIdentifier;

public abstract class Resolver {
	private Resolver parent;
	
	public static final Value NULL = v -> Resolver.NULL;
	
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
			Value ret = this.resolver.exists(this.path);
			
			if (ret != null) {
				return ret.call(v);
			} else if (v instanceof ValueIdentifier) {
				String[] npath = new String[this.path.length + 1];
				System.arraycopy(this.path, 0, npath, 0, this.path.length);
				npath[this.path.length] = ((ValueIdentifier) v).name;
				
				return new ValueResolver(this.resolver, npath);
			}else {
				return NULL;
			}
		}
	}
}
