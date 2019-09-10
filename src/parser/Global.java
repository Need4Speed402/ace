package parser;

import java.util.HashMap;

import parser.token.TokenInteger;
import value.Value;
import value.ValueIdentifier;
import value.ValuePackage;

public class Global extends Local{
	public final static Global global = new Global();
	
	public Global () {
		super((Local) null, new ValueIdentifier[0], null);
	}
	
	public static String getText (Value e) {
		if (e == Value.NULL) return "null";
		
		StringBuilder b = new StringBuilder ();
		
		if (b.length() == 0) e.call("~=").call(Global.Boolean).call("?").call(new Value(p -> {
			e.call("??").call(new Value(p2 -> {
				b.append("true");
				return Value.NULL;
			})).call("~").call(new Value(p2 -> {
				b.append("false");
				return Value.NULL;
			}));
			
			return Value.NULL;
		}));
		
		if (b.length() == 0) e.call("~=").call(Global.Integer).call("?").call(new Value(p -> {
			b.append(TokenInteger.getInt(e).toString());
			
			return Value.NULL;
		}));
		
		if (b.length() == 0) e.call("~=").call(Global.String).call("?").call(new Value(p -> {
			e.call("for").call(new Value(p2 -> {
				b.append((char) TokenInteger.getInt(p2).intValue());
				
				return Value.NULL;
			}));
			
			return Value.NULL;
		}));
		
		if (b.length() == 0) e.call("~=").call(Global.Array).call("?").call(new Value(p -> {
			b.append("Array[");
			
			e.call("for").call(new Value(p2 -> {
				if (b.length() > 6) {
					b.append("; ");
				}
				
				b.append(getText(p2));
				
				return Value.NULL;
			}));
			
			b.append("]");
			
			return Value.NULL;
		}));
		
		if (b.length() == 0) e.call("~=").call(Global.Iterator).call("?").call(new Value(p -> {
			b.append("Iterator[");
			
			e.call("for").call(new Value(p2 -> {
				if (b.length() > 9) {
					b.append("; ");
				}
				
				b.append(getText(p2));
				
				return Value.NULL;
			}));
			
			b.append("]");
			
			return Value.NULL;
		}));
		
		if (b.length() == 0) e.call("~=").call(Global.Object).call("?").call(new Value(p -> {
			b.append("Object{}");
			return Value.NULL;
		}));
		
		if (b.length() == 0) e.call("~=").call(Global.Class).call("?").call(new Value(p -> {
			b.append("Class{}");
			return Value.NULL;
		}));
		
		if (b.length() == 0) {
			if (e instanceof ValueIdentifier && !((ValueIdentifier) e).hasReference()) {
				return "Empty identifier: " + ((ValueIdentifier) e).id;
			}
			
			return "Arbitrary function";
		}else {
			return b.toString();
		}
		
		/*e = e.call("toString");
		
		if (e == Value.NULL) {
			return "null";
		}else {
			StringBuilder b = new StringBuilder();
			
			e.call("@").call(new Value(p1 -> {
				b.append((char) TokenInteger.getInt(p1).intValue());
				
				return Value.NULL;
			}));
			
			return b.toString();
		}*/
	}
		
	public static Value Class = Value.NULL, Object = Value.NULL;
	
	public static Value Boolean = Value.NULL, TRUE = Value.NULL, FALSE = Value.NULL;
	public static Value Integer = Value.NULL, Iterator = Value.NULL, String = Value.NULL, Array = Value.NULL;
	
	static {
		global.put("package", new ValuePackage());
		global.put("console", new Value(p1 -> {
			if (p1.compare("println")) {
				
				return new Value (p2 -> {System.out.println(getText(p2)); return Value.NULL;});
			}else if (p1.compare("print")) {
				return new Value (p2 -> {System.out.print(getText(p2)); return Value.NULL;});
			}else if (p1.compare("test")) {
				System.out.println("Test print");
			}
			
			return Value.NULL;
		}));
		
		global.put("Class", Class = Packages.getPackage("Class.ace"));
		global.put("Object", Object = Packages.getPackage("Object.ace"));
		
		global.put("Boolean", Boolean = Packages.getPackage("Boolean.ace"));
		global.put("true", TRUE = Boolean.call(new Value (v1 -> new Value (v2 -> v1))));
		global.put("false", FALSE = Boolean.call(new Value (v1 -> new Value (v2 -> v2))));
		
		global.put("Dynamic", Packages.getPackage("Dynamic.ace"));
		global.put("Iterator", Iterator = Packages.getPackage("Iterator.ace"));
		global.put("Integer", Integer = Packages.getPackage("Integer.ace"));
		global.put("Array", Array = Packages.getPackage("Array.ace"));
		
		//global.put("int2string", new Value (p1 -> TokenString.createString(TokenInteger.getInt(p1).toString())));
		
		global.put("String", String = Packages.getPackage("String.ace"));
	}
	
	public final HashMap<String, ValueIdentifier> map = new HashMap<>();
	
	public ValueIdentifier define (String name) {
		ValueIdentifier v = map.get(name);
		
		if (v == null) {
			v = new ValueIdentifier(name);
			
			ValueIdentifier[] n = new ValueIdentifier[this.scope.length + 1];
			System.arraycopy(this.scope, 0, n, 0, this.scope.length);
			n[n.length - 1] = v;
			
			map.put(name, v);
			
			this.scope = n;
		}
		
		return v;
	}
	
	public void put (String name, Value v) {
		this.define(name).setReference(v);
	}
}
