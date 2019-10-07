package parser;

import java.util.HashMap;

import value.Value;
import value.ValueIdentifier;
import value.ValuePackage;

public class Global extends Local{
	public final static Global global = new Global();
	
	public Global () {
		super(null, new ValueIdentifier[0]);
	}
		
	public static Value Class = Value.NULL, Object = Value.NULL;
	
	public static Value Boolean = Value.NULL, TRUE = Value.NULL, FALSE = Value.NULL;
	public static Value Integer = Value.NULL, Iterator = Value.NULL, String = Value.NULL, Array = Value.NULL;
	
	static {
		global.put("package", new ValuePackage());

		global.put("Class", Class = Packages.getPackage("Class.ace"));
		global.put("Object", Object = Packages.getPackage("Object.ace"));
		
		global.put("Boolean", Boolean = Packages.getPackage("Boolean.ace"));
		global.put("true", TRUE = Packages.getPackage("true.ace"));
		global.put("false", FALSE = Packages.getPackage("false.ace"));
		
		global.put("Dynamic", Packages.getPackage("Dynamic.ace"));
		global.put("Iterator", Iterator = Packages.getPackage("Iterator.ace"));
		global.put("Integer", Integer = Packages.getPackage("Integer.ace"));
		global.put("Array", Array = Packages.getPackage("Array.ace"));
		
		global.put("String", String = Packages.getPackage("String.ace"));
		
		global.put("console", Packages.getPackage("console.ace"));
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
