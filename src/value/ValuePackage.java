package value;

import java.io.File;
import java.util.HashMap;

import parser.Main;
import parser.Packages;

public class ValuePackage implements Value{
	@Override
	public Value call(Value p) {
		if (Value.compare(p, "unsafe")) {
			return new Unsafe("/");
		}else {
			return new Directory(Main.path).call(p);
		}
	}
	
	public static class Unsafe implements Value {
		private static HashMap<String, Value> cache = new HashMap<>();
		
		private final String path;
		
		public Unsafe (String path) {
			this.path = path;
		}
		
		@Override
		public Value call(Value p1) {
			if (p1 instanceof ValueIdentifier) {
				String p = path + ((ValueIdentifier) p1).id;
				
				if (cache.containsKey(p)) {
					return cache.get(p);
				}else {
					Value v = null;
					
					if (ValuePackage.class.getClassLoader().getResource("unsafe" + p + ".class") != null) {
						try{
							v = (Value) Class.forName("unsafe" + p.replaceAll("/", ".")).newInstance();
						}catch (Exception e) {}
					}
					
					if (v == null) v = new Unsafe(p + "/");
					
					cache.put(p, v);
					return v;
				}
			}
			
			return Value.NULL;
		}
	}

	public static class Directory implements Value {
		public final File root;
		
		public Directory(File root) {
			this.root = root;
		}
		
		@Override
		public Value call(Value p1) {
			if (p1 instanceof ValueIdentifier) {
				File cur = new File(root, ((ValueIdentifier) p1).id + ".ace");
				
				if (cur.isFile()) {
					return Packages.file(cur.toString());
				}else {
					cur = new File(Main.path, ((ValueIdentifier) p1).id);
					
					if (cur.isDirectory()) {
						return new Directory(cur);
					}
				}
			}
			
			return Value.NULL;
		}
	}
}
