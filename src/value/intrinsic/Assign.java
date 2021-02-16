package value.intrinsic;

import value.Value;
import value.ValueFunction;
import value.node.NodeIdentifier;
import value.resolver.Resolver;

public class Assign implements Value{
	public static final Value instance = new ValueFunction(name -> new ValueFunction(value -> {
		// 'name' is the identifier used to derive what this value's identity should be
		// 'value' is the base value on which to apply a new identity.
		//Every value in ace has two parts, the behavior it will exhibit when it is called,
		// and what its identity is. The assign operator lets you choose what both behaviors
		// will be individually.
		return create(name, value);
	}));
	
	public static Value create (Value name, Value value) {
		if (name == value) return name;
		
		if (name instanceof Assign) name = ((Assign) name).name;
		if (value instanceof Assign) value = ((Assign) value).value;
		
		return new Assign(name, value);
	}
	
	private final Value name, value;
	
	private Assign (Value name, Value value) {
		this.name = new IdentityCache(name);
		this.value = value;
	}
	
	@Override
	public Value resolve(Resolver res) {
		return create(this.name.resolve(res), this.value.resolve(res));
	}
	
	@Override
	public Value call (Value v) {
		return this.value.call(v);
	}
	
	@Override
	public int getID () {
		return this.name.getID();
	}
	
	@Override
	public String toString() {
		return Value.print("Assign", this.name, this.value);
	}
	
	public static class IdentityCache implements Value{
		private final int id;
		private final Value value;

		public IdentityCache (int id) {
			this.value = null;
			this.id = id;
		}
		
		public IdentityCache (Value value) {
			this.value = value;
			this.id = value.getID();
		}
		
		@Override
		public Value resolve(Resolver resolver) {
			if (this.id == -1) {
				return new IdentityCache(this.value.resolve(resolver));
			}else {
				return this;
			}
		}
		
		@Override
		public int getID() {
			return this.id;
		}
		
		@Override
		public Value call(Value v) {
			throw new Error("Identity cache is only designed to handle identity querries");
		}
		
		@Override
		public String toString() {
			if (this.id == -1) {
				return this.value.toString();
			}else {
				return NodeIdentifier.asString(this.id);
			}
		}
	}
}
