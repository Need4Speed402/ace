package value.node;

import java.util.HashMap;

import parser.Color;
import parser.Stream;
import parser.token.Resolver;
import parser.token.resolver.Source;
import parser.token.resolver.Unsafe;
import parser.token.resolver.Virtual;
import parser.token.syntax.TokenString;
import value.Value;
import value.ValueFunction;
import value.ValuePartial.Probe;

public class NodeIdentifier implements Node, Value {
	public final static Value NULL = new Null();
	
	private static int counter = 0;
	public final int id;
	
	private static HashMap<Integer, String> builtinSearch;
	
	private static void searchUnsafe (Resolver current, String dir) {
		if (current instanceof Virtual) {
			Resolver[] resolvers = ((Virtual) current).getResolvers();
			
			for (Resolver r : resolvers) {
				searchUnsafe(r, dir == null ? r.getName() : dir + " " + r.getName());
			}
		}else if (current instanceof Source) {
			Node source = ((Source) current).getSource();
			
			if (source instanceof NodeIdentifier) {
				builtinSearch.put(((NodeIdentifier) source).id, dir);
			}
		}
	}
	
	private static HashMap<Integer, String> getBuiltinSearch () {
		if (builtinSearch == null) {
			builtinSearch = new HashMap<>();
			searchUnsafe(Unsafe.instance, null);
		}
		
		return builtinSearch;
	}
	
	protected NodeIdentifier() {
		this.id = ++counter;
	}
	
	@Override
	public Value run(Value environment) {
		//System.out.println(environment);
		return environment.call(this);
	}
	
	@Override
	public Value call(Value v) {
		return NULL.call(v);
	}
	
	@Override
	public Value getID(Getter getter) {
		return getter.resolved(id);
	}
	
	@Override
	public String toString() {
		return asString(this.id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NodeIdentifier) {
			return ((NodeIdentifier) obj).id == this.id;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	public static String asString (int id) {
		{
			String rev = getBuiltinSearch().get(id);
			
			if (rev != null) {
				return "[" + Color.purple(rev) + "]";
			}
		}
		
		String name = Node.ids_rev.get(id);
		if (name == null) name = Integer.toString(id);
		
		boolean isSpecial = name.isEmpty();
		
		if (!isSpecial) {
			Stream s = new Stream(name);
			
			while (s.hasChr()) {
				if (s.next(Stream.whitespace) || s.next("{}[]();\"\'".toCharArray())) {
					isSpecial = true;
					break;
				}
				
				s.chr();
			}
		}
		
		if (isSpecial) {
			return new TokenString (name).toString();
		}else {
			return name;
		}
	}
	
	private static class Null extends ValueFunction {
		public Null () {
			super (v -> NodeIdentifier.NULL);
		}
		
		@Override
		public ValueFunction resolve(Probe probe, Value value) {
			return this;
		}
		
		@Override
		public String toString() {
			return "NodeIdentifier.Null";
		}
	}
}
