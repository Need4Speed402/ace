package parser.token;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import node.Node;
import node.NodeCall;
import node.NodeIdentifier;
import parser.ParserException;
import parser.Stream;
import parser.TokenList;
import parser.unicode.Unicode;

public class TokenString extends TokenBlock{
	public static HashMap<String, String> unicodeNames = Unicode.getLookup();
	

	public TokenString (Token[] tokens) {
		super(tokens);
	}
	
	public static Node[] createStringElements (String s){
		Node[] string = new Node[s.length()];
		
		for (int ii = 0; ii < string.length; ii++) {
			string[ii] = Node.pipe("Integer", "Iterator", TokenInteger.getEvents(TokenInteger.fromInt(BigInteger.valueOf(s.charAt(ii)))));
		}
		
		return string;
	}
	
	@Override
	public Node createEvent() {
		Node stack = null;
		
		for (Token t : this.getTokens()) {
			if (stack == null) {
				stack = t.createEvent();
			}else {
				stack = new NodeCall(new NodeCall(stack, new NodeIdentifier("+")), t.createEvent());
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
		public Node createEvent() {
			return Node.pipe("String", "Iterator", createStringElements(this.text));
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
		public StringBuilder b = new StringBuilder();
		
		public BreakLocation (int tokenIndex, int index) {
			this.tokenIndex = tokenIndex;
			this.index = index;
		}
		
		public void append (char c) {
			b.append(c);
		}
		
		@Override
		public String toString() {
			return b.toString();
		}
		
		public int length () {
			return b.length();
		}
		
		public char charAt (int i) {
			return b.charAt(i);
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
			
			if (s.next('\n')) {
				BreakLocation br = new BreakLocation(tokens.size(), current.length());
				breaks.add(br);
				
				while (!s.isNext('\n') && s.isNext(Stream.whitespace)) {
					if (s.next('\r')) continue;
					br.append(s.chr());
				}
				
				current.append('\n').append(br);
				continue;
			}
			
			if (s.next('`')) {
				while (s.next('\r'));
				
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
			
			if (first.tokenIndex == 0 && first.index == 0 && last.tokenIndex == tokens.size() - 1) {
				int len = 0;
				if (breaks.size() > 1) main: while (true) {
					if (breaks.get(0).length() <= len) break;
					
					char c = breaks.get(0).charAt(len);
					
					for (int i = 1; i < breaks.size() - 1; i++) {
						if (breaks.get(i).length() <= len || c != breaks.get(i).charAt(len)) break main;
					}
					
					len++;
				}
				
				if (last.index == tokens.get(tokens.size() - 1).toString().length() - last.length() - 1) {
					if (len > 0) for (int i = 0; i < breaks.size() - 1; i++) {
						BreakLocation br = breaks.get(i);
						String str = tokens.get(br.tokenIndex).toString();
						
						tokens.set(br.tokenIndex, new StringSegment(str.substring(0, br.index + 1) + str.substring(br.index + 1 + len)));
						
						for (int ii = i + 1; ii < breaks.size(); ii++) {
							if (breaks.get(ii).tokenIndex != br.tokenIndex) break;
							breaks.get(ii).index -= len;
						}
					}
					
					String tokenLast = tokens.get(tokens.size() - 1).toString();
					tokens.set(tokens.size() - 1, new StringSegment(tokenLast.substring(0, tokenLast.length() - last.length() - 1)));
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
