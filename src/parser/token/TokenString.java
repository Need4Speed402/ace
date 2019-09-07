package parser.token;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import event.Event;
import event.EventCall;
import event.EventDynamic;
import event.EventIdentifier;
import parser.ParserException;
import parser.Stream;
import parser.TokenList;
import parser.unicode.Unicode;
import unsafe.Memory;
import value.Value;

public class TokenString extends TokenBlock{
	public static HashMap<String, String> unicodeNames = Unicode.getLookup();
	

	public TokenString (Token[] tokens) {
		super(tokens);
	}
	
	public static Value createStringMemory (String s){
		Value[] string = new Value[s.length()];
		
		for (int ii = 0; ii < string.length; ii++) {
			string[ii] = TokenInteger.toValue(TokenInteger.fromInt(BigInteger.valueOf(s.charAt(ii))));
		}
		
		return new Memory.Allocate(string);
	}
	
	@Override
	public Event createEvent() {
		Event stack = null;
		
		for (Token t : this.getTokens()) {
			if (stack == null) {
				stack = t.createEvent();
			}else {
				stack = new EventCall(new EventCall(stack, new EventIdentifier("+")), t.createEvent());
			}
		}
		
		return stack;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		boolean escaped = false;
		
		for (Token t : this.getTokens()) {
			if (t instanceof StringSegment) {
				escaped |= t.toString().indexOf('\'') >= 0;
			}
		}
		
		b.append(escaped ? '"' : '\'');
		
		for (Token t : this.getTokens()) {
			if (t instanceof StringSegment) {
				b.append(((StringSegment) t).toString(escaped));
			}else {
				b.append('`').append(t.toString());
			}
		}
		
		b.append(escaped ? "`\"" : '\'');
		
		return b.toString();
	}
	
	public static class StringSegment extends Token {
		private final String text;
		
		public StringSegment (String text) {
			this.text = text;
		}
		
		@Override
		public Event createEvent() {
			return new EventCall(new EventIdentifier("String"), new EventDynamic(() -> createStringMemory(this.text)));
		}
		
		@Override
		public String toString() {
			return this.text;
		}
		
		public String toString (boolean escaped) {
			StringBuilder b = new StringBuilder ();
			
			for (int i = 0; i < this.text.length(); i++) {
				char c = this.text.charAt(i);
				
				if (!escaped && c == '\'') b.append("`'");
				else if (c == '`') b.append("``");
				else if (c == '\n') b.append("`n");
				else if (c == '\r') b.append("`r");
				else if (c == '\t') b.append("`t");
				else if (c == '\f') b.append("`f");
				else if (c == '\b') b.append("`b");
				else if (c < 32 || c > 126) b.append('`' + Integer.toString(c, 16).toUpperCase() + '^');
				else b.append(c);
			}
			
			return b.toString();
		}
	}
	
	private static class BreakLocation {
		public int tokenIndex, index;
		
		public BreakLocation (int tokenIndex, int index) {
			this.tokenIndex = tokenIndex;
			this.index = index;
		}
	}
	
	public static TokenString readString (Stream s, char escape) {
		TokenList tokens = new TokenList();
		
		StringBuilder current = new StringBuilder();
		
		List<BreakLocation> breaks = new ArrayList<>();
		
		while (true) {
			if (!s.hasChr()) {
				if (escape == '\0') {
					break;
				}else {
					throw new ParserException("unexpected end of input when reading string");
				}
			}
			
			if (escape == '\'' && s.next('\'')) break;
			if (escape == '\0' && s.next('\n')) break;
			
			if (s.next('\r')) continue;
			
			if (s.isNext('\n')) {
				while (s.hasChr()) {
					if (s.next('\n')) {
						breaks.add(new BreakLocation(tokens.size(), current.length()));
						
						current.append('\n');
					}
					if (!s.next(Stream.whitespace)) break;
				}
				
				continue;
			}
			
			if (s.next('`')) {
				if (escape == '"' && s.next('"')) break;
				else if (s.next('t')) current.append('\t');
				else if (s.next('n')) current.append('\n');
				else if (s.next('r')) current.append('\r');
				else if (s.next('f')) current.append('\f');
				else if (s.next('b')) current.append('\b');
				else if (s.next('`')) current.append('`');
				else if (s.next('[')) {
					StringBuilder name = new StringBuilder();
					
					boolean whitespace = true;
					char[] ignoreCharaters = " _-\t".toCharArray();
					
					while (s.hasChr()) {
						if (s.next(']')) {
							break;
						}else if (s.next(ignoreCharaters)) {
							whitespace = true;
						}else{
							if (whitespace && name.length() > 0) {
								name.append(' ');
							}
							
							whitespace = false;
							name.append(s.chr());
						}
					}
					
					String decoded = unicodeNames.get(name.toString().toLowerCase());
					
					if (decoded == null) throw new ParserException("No known unicode literal: " + name);
					
					current.append(decoded);
				}else if (s.next('(')) {
					if (current.length() > 0) {
						tokens.push(new StringSegment(current.toString()));
						current.setLength(0);
					}
					
					tokens.push(new TokenScope(s));
				}else{ //look ahead to see if there is a number
					StringBuilder ahead = new StringBuilder ();
					
					char[] validLookaheadChars = ",0123456789ABCDEFabcdef".toCharArray();
					char[] breakCharacters = "!^*.".toCharArray();
					
					while (s.hasChr()) {
						if (s.isNext(breakCharacters)) {
							ahead.append(s.chr());
							Object val = TokenInteger.parseNumber(ahead.toString());
							
							if (val instanceof BigInteger) {
								ahead.setLength(0);
								current.append((char) ((BigInteger) val).intValue());
							}
							
							break;
						}else if (ahead.length() == 0 || s.isNext(validLookaheadChars)) {
							ahead.append(s.chr());
						}else {
							break;
						}
					}
					
					if (ahead.length() > 0) {
						if (ahead.charAt(0) == '0') {
							ahead.deleteCharAt(0);
							current.append('\0');
						}
						
						current.append(ahead.toString());
					}
				}
				
				continue;
			}
			
			current.append(s.chr());
		}
		
		if (current.length() > 0) {
			tokens.push(new StringSegment(current.toString()));
		}else if (tokens.size() == 0) {
			tokens.push(new StringSegment(""));
		}
		
		if (breaks.size() >= 2){
			BreakLocation first = breaks.get(0);
			BreakLocation last = breaks.get(breaks.size() - 1);
			
			if (first.tokenIndex == 0 && last.tokenIndex == tokens.size() - 1) {
				String tokenLast = tokens.get(tokens.size() - 1).toString();
				
				if (first.index == 0 && last.index == tokenLast.toString().length() - 1) {
					tokens.set(tokens.size() - 1, new StringSegment(tokenLast.substring(0, tokenLast.length() - 1)));
					tokens.set(0, new StringSegment(tokens.get(0).toString().substring(1)));
				}
			}
		}
		
		return new TokenString (tokens.toArray());
	}
	
	public static TokenString readEscapedString (Stream s) {
		return readString(s, '"');
	}
	
	public static TokenString readString (Stream s) {
		return readString(s, '\'');
	}
}
