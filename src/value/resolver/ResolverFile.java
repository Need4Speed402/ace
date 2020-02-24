package value.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ResolverFile extends ResolverVirtual{
	private File root;
	
	public ResolverFile(File root, Pair ... pairs) {
		super(pairs);
		this.root = root;
	}
	
	public ResolverFile(File root) {
		this.root = root;
	}
	
	@Override
	public Pair[] getPairs () {
		ArrayList<Pair> pairs = new ArrayList<>();
		pairs.addAll(Arrays.asList(super.getPairs()));
		
		File[] files = this.root.listFiles();
		
		for (File file : files) {
			String name = file.getName();
			
			if (name.toLowerCase().endsWith(".ace") && file.isFile()) {
				pairs.add(new Pair(name.substring(0, name.length() - 4), new ResolverSource(file)));
			}
		}
		
		for (File file : files) {
			if (file.isDirectory()) {
				pairs.add(new Pair(file.getName(), new ResolverFile(file)));
			}
		}
		
		return pairs.toArray(new Pair[0]);
	}
}
