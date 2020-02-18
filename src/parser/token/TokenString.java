package parser.token;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import value.node.Node;
import parser.Color;
import parser.ParserException;
import parser.Stream;
import parser.TokenList;
import parser.unicode.Unicode;

public class TokenString extends TokenProcedure{
	public static HashMap<String, String> unicodeNames = Unicode.getLookup();
	public static HashMap<String, String> inverseUnicodeNames = Unicode.getInverse();

	public TokenString (Token[] tokens) {
		super(tokens);
	}
	
	public static Node createStringElement (String s){
		Node[] string = new Node[s.length()];
		
		for (int ii = 0; ii < string.length; ii++) {
			string[ii] = Node.call("Integer", new TokenInteger.BooleanArray(s.charAt(ii)).toNode());
		}
		
		return TokenProcedure.createBlock(string);
	}
	
	@Override
	public Node createNode() {
		Node stack = null;
		
		for (Token t : this.getTokens()) {
			if (stack == null) {
				stack = t.createNode();
			}else {
				stack = Node.call(stack, Node.id("+"), t.createNode());
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
				b.append(Color.purple("`" + t.toString()));
			}
		}
		
		b.append(escaped ? "`\"" : '\'');
		
		return Color.yellow(b.toString());
	}
	
	public static class StringScope extends TokenScope {
		public StringScope (Stream s){
			super (s);
		}
		
		@Override
		public Node createNode() {
			return Node.call("String", super.createNode());
		}
	}
	
	public static class StringSegment implements Token {
		private final String text;
		
		public StringSegment (String text) {
			this.text = text;
		}
		
		@Override
		public Node createNode() {
			return Node.call("String", createStringElement(this.text));
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
				else if (c < 32 || c > 126) {
					String n = inverseUnicodeNames.get(String.valueOf(c));
					
					if (n != null) {
						if (n.length() == 1) {
							b.append(Color.purple("`" + n));
						}else {
							b.append(Color.purple("`[" + n + ']'));
						}
					}else {
						b.append(Color.purple('`' + Integer.toString(c, 16).toUpperCase() + '^'));
					}
				}
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
				else if (s.next('(')) {
					if (current.length() > 0) {
						tokens.push(new StringSegment(current.toString()));
						current.setLength(0);
					}
					
					tokens.push(new StringScope(s));
				}else if (s.next('[')) {
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
					
					String decoded = unicodeNames.get(name.toString());
					
					if (decoded == null) throw new ParserException("No known unicode literal: " + name);
					
					current.append(decoded);
				}else{
					//look ahead to see if there is a number
					BigInteger i = (BigInteger) TokenInteger.readNum(s, true);
					
					if (i != null) {
						current.append((char) i.intValue());
					}else {
						String chr = String.valueOf(s.chr());
						String decoded = unicodeNames.get(chr);
						
						if (decoded == null) decoded = chr;
						current.append(decoded);
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
