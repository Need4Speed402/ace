package parser.resolver;

import java.io.File;
import java.util.HashMap;

import parser.Packages;
import value.Value;
import value.ValueIdentifier;

public class ResolverFile implements Resolver{
	private File file;
	private static HashMap<String, Value> resolutions = new HashMap<>();
	
	public ResolverFile(File file) {
		this.file = file;
	}
	
	@Override
	public Value call(Value value) {
		if (!(value instanceof ValueIdentifier)) return Value.NULL;
		String name = ((ValueIdentifier) value).name;
		
		File file = new File(this.file, name);
		
		Value resolution = resolutions.get(file.getAbsolutePath());
		if (resolution != null) return resolution;
		
		if (new File(file.getAbsolutePath() + ".ace").isFile()) {
			resolution = p -> Packages.file(file.getAbsolutePath() + ".ace").call(p);
		}else if (file.isDirectory()){
			resolution = new ResolverFile(file);
		}else {
			resolution = Resolver.NULL;
		}
		
		resolutions.put(file.getAbsolutePath(), resolution);
		return resolution;
	}
}
