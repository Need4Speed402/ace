package parser.unicode;

import java.util.HashMap;

import parser.Stream;
import parser.token.Token;
import parser.token.TokenString;

public class Unicode {
	private static final HashMap<String, String> lookup, inverse;
	
	static {
		Stream s = new Stream(Unicode.class.getResourceAsStream("UnicodeNames.txt"));
		
		lookup = new HashMap<String, String>();
		inverse = new HashMap<String, String>();
		
		while (s.hasChr()) {
			StringBuilder bind = new StringBuilder();
			
			while (s.hasChr()) {
				if (s.next('-')) break;
				bind.append(s.chr());
			}
			
			while (s.next(Stream.whitespace));
			
			Token[] tokens = TokenString.readString(s, '\0').getTokens();
			
			if (tokens.length != 1) throw new RuntimeException("Could not parse unicode table");
			if (!(tokens[0] instanceof TokenString.StringSegment)) throw new RuntimeException("Could not parse unicode table");
			
			String key = bind.toString().trim();
			String value = ((TokenString.StringSegment) tokens[0]).toString();
			
			lookup.put(key, value);
			if (!inverse.containsKey(value)) inverse.put(value, key);
		}
	}
	
	public static HashMap<String, String> getLookup () {
		return lookup;
	}
	
	public static HashMap<String, String> getInverse (){
		return inverse;
	}
}
