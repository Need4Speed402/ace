package value;

public class ValueIdentifier implements Value {
	public final String id;
	private Value referenced;
	private final ValueIdentifier parent;
	
	public ValueIdentifier (String id) {
		this(id, null, null);
	}
	
	public ValueIdentifier(ValueIdentifier parent) {
		this(parent.id, parent, null);
	}
	
	public ValueIdentifier getParent () {
		ValueIdentifier i = this.parent;
		
		while (true) {
			if (i == null) return null;
			if (i.referenced != null) return i;
			i = i.parent;
		}
	}
	
	public ValueIdentifier getFirstParent () {
		ValueIdentifier i = this;
		
		while (true) {
			if (i == null) return null;
			if (i.referenced != null) return i.getParent();
			i = i.parent;
		}
	}
	
	public boolean hasReference () {
		ValueIdentifier i = this;
		
		while (true) {
			if (i == null) return false;
			if (i.referenced != null) return true;
			i = i.parent;
		}
	}
	
	public Value getReference () {
		ValueIdentifier i = this;
		
		while (true) {
			if (i == null) return Value.NULL;
			if (i.referenced != null) return i.referenced;
			i = i.parent;
		}
	}
	
	private ValueIdentifier(String id, ValueIdentifier parent, Value referenced) {
		this.id = id;
		this.parent = parent;
		this.referenced = referenced;
	}
	
	@Override
	public Value call(Value p) {
		if (Value.compare(p, ">$")){
			return new IdentifierReference(this.getFirstParent()) {
				@Override
				public Value call(Value p2) {
					if (referenced == null && Value.compare(p2, "=")) {
						return p3 -> {
							if (referenced != null) return Value.NULL;
							
							setReference(p3);
							
							return Value.NULL;
						};
					}
					
					return super.call(p2);
				}
			};
		}else{
			return this.getReference().call(p);
		}
	}
	
	public void setReference (Value v) {
		if (v instanceof ValueIdentifier) {
			v = ((ValueIdentifier) v).getReference();
		}
		
		this.referenced = v;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + this.id + "]";
	}
	
	public static class IdentifierReference implements Value {
		public final ValueIdentifier ref;
		
		public IdentifierReference(ValueIdentifier ref) {
			this.ref = ref;
		}
		
		@Override
		public Value call(Value p) {
			if (this.ref == null) return Value.NULL;
			
			if (Value.compare(p, ">$")) {
				ValueIdentifier parent = this.ref.getParent();
				
				if (parent != null) {
					return new IdentifierReference(parent);
				}
			}
			
			return this.ref.referenced.call(p);
		}
	}
}
