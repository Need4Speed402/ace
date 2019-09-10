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
	
	public boolean hasReference () {
		ValueIdentifier i = this;
		
		while (i != null) {
			if (i.referenced != null) return true;
			i = i.parent;
		}
		
		return false;
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
				return new Value (p2 -> {
					if (this.referenced == null && p2.compare("=")) {
						return new Value(p3 -> {
							this.setReference(p3);
							return Value.NULL;
						});
					}else{
						return this.getReference().call(p2);
					}
				});
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
}
