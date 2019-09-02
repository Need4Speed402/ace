package parser.unicode;

import java.util.HashMap;

import parser.Stream;
import parser.token.Token;
import parser.token.TokenString;

public class Unicode {
	public static HashMap<String, String> getLookup () {
		Stream s = new Stream(Unicode.class.getResourceAsStream("UnicodeNames.txt"));
		
		HashMap<String, String> map = new HashMap<String, String>();
		
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
			
			map.put(bind.toString().trim(), ((TokenString.StringSegment) tokens[0]).toString());
		}
		
		System.out.println(map);
		
		return map;
	}
}
