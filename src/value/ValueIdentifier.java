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
	
	private ValueIdentifier(String id, ValueIdentifier parent, Value referenced) {
		this.id = id;
		this.parent = parent;
		this.referenced = referenced;
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
	
	@Override
	public Value call(Value p) {
		if (Value.compare(p, "`>`")){
			return p2 -> {
				if (this.referenced != null) {
					return this.referenced.call(p2);
				}
				
				if (Value.compare(p2, "=")) {
					return p3 -> {
						this.setReference(p3);
						return Value.NULL;
					};
				}
				
				return Value.NULL;
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
}
