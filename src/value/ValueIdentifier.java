package value;

public class ValueIdentifier extends Value {
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
		ValueIdentifier i = this;
		
		while (i != null) {
			if (i.referenced != null) return i;
			i = i.parent;
		}
		
		return null;
	}
	
	public Value getReference () {
		ValueIdentifier i = this;
		
		while (i != null) {
			if (i.referenced != null) return i.referenced;
			i = i.parent;
		}
		
		return Value.NULL;
	}
	
	private ValueIdentifier(String id, ValueIdentifier parent, Value referenced) {
		super (null);
		
		this.id = id;
		this.parent = parent;
		this.referenced = referenced;
		
		this.function = p -> {
			if (p.compare(">$")){
				return new IdentifierReference(this);
			}else{
				return this.getReference().call(p);
			}
		};
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
	
	@Override
	public boolean compare(String ident) {
		return ident == this.id;
	}
	
	public static class IdentifierReference extends Value {
		public IdentifierReference(ValueIdentifier ref) {
			super(p -> {
				if (ref.compare(">$")) {
					ValueIdentifier parent = ref.getParent();
					
					if (parent != null) {
						return new IdentifierReference(parent);
					}
				}
				
				if (ref.referenced == null && p.compare("=")) {
					return new Value(val -> {
						//TODO: return error when identifier already has value
						ref.setReference(val);
						return Value.NULL;
					});
				}
				
				return ref.referenced.call(p);
			});
		}
		
	}
}
