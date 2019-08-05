package value;

import java.io.File;
import java.util.HashMap;

import parser.Main;
import parser.Packages;

public class ValuePackage extends Value{

	public ValuePackage() {
		super(p1 -> {
			if (p1.compare("unsafe")) {
				return new Unsafe("/");
			}else {
				return new Directory(Main.path).call(p1);
			}
		});
	}
	
	public static class Unsafe extends Value {
		private static HashMap<String, Value> cache = new HashMap<>();
		
		public Unsafe (String path) {
			super(p1 -> {
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
			});
		}
	}

	public static class Directory extends Value {
		public Directory(File root) {
			super (p1 -> {
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
			});
		}
	}
}
