package value;

public class ValueIdentifier implements Value {
	public final String id;
	private Value referenced;
	private final ValueIdentifier parent;
	
	public ValueIdentifier (String id, Value referenced) {
		this(id, null, referenced);
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
			if (i.referenced != null) return true;
			i = i.parent;
		}
	}
	
	public Value getReference () {
		ValueIdentifier i = this;
		
		while (true) {
			if (i.referenced != null) return i.referenced;
			i = i.parent;
		}
	}
	
	@Override
	public Value call(Value p) {
		if (Value.compare(p, "`>`")){
			return p2 -> {
				if (Value.compare(p2, "=")) return p3 -> {
					if (p3 instanceof ValueIdentifier) {
						this.referenced = ((ValueIdentifier) p3).getReference();
					}else {
						this.referenced = p3;
					}
					
					return Value.NULL;
				};
				
				return this.referenced.call(p2);
			};
		}else{
			return this.getReference().call(p);
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + this.id + "]";
	}
}
