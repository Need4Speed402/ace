package parser.token;

import java.math.BigInteger;
import java.util.ArrayList;

import event.Event;
import event.EventCall;
import event.EventDynamic;
import event.EventIdentifier;
import parser.Global;
import parser.Parser;
import unsafe.Memory;
import value.Value;

public class TokenString extends TokenCompound{
	private ArrayList<Segment> segments = new ArrayList<>();
	public final char initiator;
	
	public TokenString (char c) {
		this.initiator = c;
	}
	
	public static String unescape (String s) {
		StringBuilder ts = new StringBuilder();
		
		for (int i = 0; i < s.length();) {
			char c = s.charAt(i++);
			
			if (c == '\n') {
				main:for (; i < s.length();) {
					for (int ii = 0; ii < Parser.whitespace.length; ii++) {
						if (s.startsWith("\n", i)) {
							break;
						}else if (s.startsWith(Parser.whitespace[ii], i)) {
							i += Parser.whitespace[ii].length();
							continue main;
						}
					}
				
					break;
				}
				
				ts.append('\n');
			}else if (c == '`') {
				StringBuilder escape = new StringBuilder();
				int ii = i;
				while (ii < s.length()) {
					c = s.charAt(ii++);
					
					if (c == '`') break;
					escape.append(c);
				}
				
				Object num = Parser.parseNumber(escape.toString());
				
				if (num != null && num instanceof BigInteger) {
					i = ii;
					ts.append((char) ((BigInteger) num).intValue());
				}else {
					i++;
					c = escape.charAt(0);
					
					if (c == 'n') ts.append('\n');
					else if (c == 'r') ts.append('\r');
					else if (c == 't') ts.append('\t');
					else ts.append(c);
				}
			}else {
				ts.append(c);
			}
		}
		
		return ts.toString();
	}
	
	public Segment[] sanitize () {
		Segment[] san = new Segment[this.segments.size()];
		
		for (int i = 0; i < segments.size(); i++) {
			String s = segments.get(i).value;
			
			if (i == 0) {
				int start = 0;
				int count = 0;
				
				main:for (; start < s.length();) {
					if (s.charAt(start) == '\n') {
						count++;
						
						if (count == 2) {
							break;
						}else {
							start++;
						}
					}else for (int ii = 0; ii < Parser.whitespace.length; ii++) {
						if (s.startsWith(Parser.whitespace[ii], start)) {
							start += Parser.whitespace[ii].length();
							continue main;
						}
					}
					
					if (count == 0) start = 0;
					break;
				}
				
				s = s.substring(start);
			}
			
			if (i == segments.size() - 1) {
				int end = s.length() - 1;
				
				main:for (; end >= 0;) {
					if (s.charAt(end) == '\n') {
						end--;
						break;
					}else for (int ii = 0; ii < Parser.whitespace.length; ii++) {
						if (s.startsWith(Parser.whitespace[ii], end - Parser.whitespace[ii].length())) {
							end -= Parser.whitespace[ii].length();
							continue main;
						}
					}
					
					break;
				}
				
				s = s.substring(0, end + 1);
			}
			
			san[i] = new Segment(segments.get(i).index, unescape(s));
		}
		
		return san;
	}
	
	public static Value createString (String s){
		Value[] string = new Value[s.length()];
		
		for (int ii = 0; ii < string.length; ii++) {
			string[ii] = TokenInteger.toValue(TokenInteger.fromInt(BigInteger.valueOf(s.charAt(ii))));
		}
		
		return Global.String.call(new Memory.Allocate(string));
	}
	
	@Override
	public Event createEvent() {
		ArrayList<Event> events = new ArrayList<>();
		int index = 0;
		
		Segment[] segments = this.sanitize();
		
		for (int i = 0; i <= this.getLength(); i++) {
			if (index < segments.length && segments[index].index == i) {
				String s = segments[index++].value;
				
				if (s.isEmpty()) continue;
				
				events.add(new EventDynamic(() -> createString(s)));
			}
			
			if (i < this.getLength()) {
				events.add(new EventCall(this.getTokens()[i].createEvent(), new EventIdentifier("toString")));
			}
		}
		
		Event stack = null;
		
		for (int i = 0; i < events.size(); i++) {
			if (stack == null) {
				stack = events.get(i);
			}else {
				stack = new EventCall(new EventCall(stack, new EventIdentifier("+")), events.get(i));
			}
		}
		
		return stack;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		boolean found = false;
		
		Segment[] segments = this.sanitize();
		
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].value.contains("'")) {
				found = true;
				break;
			}
		}
		
		b.append(found ? '"' : '\'');
		
		int index = 0;
		
		for (int i = 0; i <= this.getLength(); i++) {
			if (index < segments.length && segments[index].index == i) {
				String s = segments[index++].value;
				
				for (int ii = 0; ii < s.length(); ii++) {
					char c = s.charAt(ii);
					
					if (c == '`') b.append("``");
					else if (c == '\n') b.append("`n");
					else if (c == '\r') b.append("`r");
					else if (c == '\t') b.append("`t");
					else b.append(c);
				}
			}
			
			if (i < this.getLength()) {
				b.append('`').append(this.getTokens()[i].toString());
			}
		}
		
		b.append(found ? "`\"" : '\'');
		
		return b.toString();
	}
	
	public void add (char c) {
		this.add(String.valueOf(c));
	}
	
	public void add (String t){
		Segment last = this.segments.isEmpty() ? null : this.segments.get(this.segments.size() - 1);
		
		if (last != null && last.index == this.getLength()) {
			last.value += t;
		}else {
			this.segments.add(new Segment(this.getLength(), t));
		}
	}
	
	public class Segment {
		int index;
		String value;
		
		public Segment (int index, String value) {
			this.index = index;
			this.value = value;
		}
	}
}
