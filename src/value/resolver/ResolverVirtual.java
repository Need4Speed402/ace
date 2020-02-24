package value.resolver;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import parser.Color;
import value.Value;

public class ResolverVirtual extends Resolver {
	protected final Pair[] pairs;
	private HashMap<String, Resolver> cache;
	
	public ResolverVirtual (Pair ... pairs) {
		this.pairs = pairs;
	}
	
	public Pair[] getPairs (){
		return this.pairs;
	}
	
	private HashMap<String, Resolver> getMap () {
		if (this.cache == null) {
			Pair[] pairs = this.getPairs();
			this.cache = new HashMap<>();
			
			for (int i = 0; i < pairs.length; i++) {
				this.cache.putIfAbsent(pairs[i].name, pairs[i].resolver);
			}
		}
		
		return this.cache;
	}
	
	@Override
	public Value call(Resolver r) {
		return v -> this.child(v.getName()).call(r);
	}
	
	public String[] children () {
		Set<String> keys = this.getMap().keySet();
		String[] values = new String[keys.size()];
		int i = 0;
		
		for (String s : keys) {
			values[i++] = s;
		}
		
		return values;
	}
	
	public Resolver child (String name) {
		return this.getMap().get(name);
	}
	
	public ResolverVirtual insertRoot (Resolver r) {
		Pair[] thispairs = this.getPairs();
		
		for (int i = 0; i < thispairs.length; i++) {
			Pair pair = thispairs[i];
			
			if (pair.name.equals("root")) {
				Pair[] pairs = new Pair[thispairs.length];
				System.arraycopy(thispairs, 0, pairs, 0, thispairs.length);
				pairs[i] = new Pair(pair.name, ((ResolverVirtual) pair.resolver).insertRoot(r));
				
				return new ResolverVirtual(pairs);
			}
		}
		
		Pair[] pairs = new Pair[thispairs.length + 1];
		System.arraycopy(thispairs, 0, pairs, 0, thispairs.length);
		pairs[thispairs.length] = new Pair ("root", r);
		return new ResolverVirtual(pairs);
	}
	
	@Override
	public String toString() {
		Set<Entry<String, Resolver>> children = this.getMap().entrySet();
		
		if (children.size() == 0) return "";
		
		StringBuilder b = new StringBuilder();
		int i = 0;
		
		for (Entry<String, Resolver> entry : children) {
			boolean last = i == children.size() - 1;
			
			if (last) b.append('\u2514');
			else b.append('\u251C');
			
			if (!(entry.getValue() instanceof ResolverSource)) {
				b.append(' ').append(entry.getKey()).append('\n');
				b.append(last ? "  " : "\u2502 ");
				
				String val = entry.getValue().toString();
				
				for (int ii = 0; ii < val.length(); ii++) {
					char c = val.charAt(ii);
					
					if (c == '\n') {
						b.append(last ? "\n  " : "\n\u2502 ");
					}else {
						b.append(c);
					}
				}
				
				b.append("\n");
			}else{
				b.append(' ').append(Color.cyan(entry.getKey())).append('\n');
			}
			
			i++;
		}
		
		return b.substring(0, b.length() - 1);
	}
	
	public static class Pair {
		public final String name;
		public final Resolver resolver;
		
		public Pair (String name, Resolver resolver) {
			this.name = name;
			this.resolver = resolver;
		}
		
		public Pair (String name, Value v) {
			this.name = name;
			this.resolver = new ResolverSource(v);
		}
	}
}
