package parser.token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import event.Event;
import event.EventCall;
import parser.ParserException;
import parser.Stream;
import parser.TokenList;

public class TokenStatement extends TokenBlock{
	public static final String operators = ".: ?,~_ @ |&! =<> +- */\\% ^ $# `";
	public static final char[] ops = operators.replaceAll(" ", "").toCharArray();
	public static final int[] operatorValues;
	
	public TokenStatement (Stream s) {
		super(TokenStatement.readStatement(s));
	}
	
	static {
		operatorValues = new int[operators.length()];
		int v = 0;
		for (int i = 0; i < operators.length(); i++) {
			if (operators.charAt(i) == ' ') {
				v++;
			}else {
				operatorValues[i] = v;
			}
		}
	}
	
	@Override
	public String toString() {
		boolean verbose = true;
		
		if (this.getTokens().length == 0) return "";
		
		if (verbose) {
			return getCaller(this.getTokens()).toString();
		}else {
			return super.toString();
		}
	}
	
	public static boolean isOperator (Token t) {
		return t instanceof TokenOperator;
	}
	
	public static boolean isModifier (Token t) {
		if (t instanceof TokenImmediate) {
			return isModifier(((TokenImmediate) t).getFirst());
		}else if (t instanceof TokenOperatorImmediate) {
			return isModifier(((TokenOperatorImmediate) t).getToken());
		}else if (t instanceof TokenImmediateArgument) {
			return isModifier(((TokenImmediateArgument) t).getContent());
		}else if (t instanceof TokenImmediateArgumentModifier) {
			return isModifier(((TokenImmediateArgumentModifier) t).getContent());
		}else if (t instanceof TokenIdentifier) {
			String s = ((TokenIdentifier) t).getName();
			
			if (s.length() == 0) return false;
			
			char c = s.charAt(0);
			
			if (Character.toLowerCase(c) == Character.toUpperCase(c)) {
				return false;
			}else if (Character.toUpperCase(c) == c) {
				for (int i = 1; i < s.length(); i++) {
					if (TokenStatement.operators.indexOf(s.charAt(i)) >= 0) {
						return false;
					}
				}
				
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	public static boolean isSetter (Token t) {
		if (t instanceof TokenOperator) {
			String s = ((TokenOperator) t).getName();
			
			if (s.length() == 0) return false;
			if (s.charAt(0) != '=') return false;
			
			for (int i = 1; i < s.length(); i++) {
				char c = s.charAt(i);
				
				if (c == '=') return false;
			}
			
			return true;
		}else {
			return false;
		}
	}
	
	public static Token linear (Token[] tokens) {
		Token t = tokens[0];
		
		for (int i = 1; i < tokens.length; i++) {
			t = new Caller(t, tokens[i]);
		}
			
		return t;
	}
	
	public static Token linearModified (Token[] tokens) {
		Token t = tokens[0];
		
		for (int i = 1; i < tokens.length; i++) {
			if (i == tokens.length - 1) {
				t = new ModifierCaller(t, tokens[i]);
			}else {
				t = new Caller(t, tokens[i]);
			}
		}
			
		return t;
	}
	
	public static Token stage1 (Token[] tokens) {
		if (tokens.length == 1) return tokens[0];
		
		//first stage, check for setter operators
		for (int i = 0; i < tokens.length; i++) {
			if (isSetter(tokens[i])) {
				if (i == 0) {
					return new Caller(tokens[i], stage1(Arrays.copyOfRange(tokens, i + 1, tokens.length)));
				}else if (i == tokens.length - 1) {
					return new Caller(stage2(Arrays.copyOfRange(tokens, 0, i)), tokens[i]);
				}else {
					return new Caller(new Caller(stage2(Arrays.copyOfRange(tokens, 0, i)), tokens[i]), stage1(Arrays.copyOfRange(tokens, i + 1, tokens.length)));
				}
			}
		}
		
		return stage2(tokens);
	}
	
	public static int comparePrecedence (Token c, Token r) {
		boolean ctoken = c instanceof TokenIdentifier;
		boolean rtoken = r instanceof TokenIdentifier;
		
		if (!ctoken && !rtoken) return 0;
		
		if (!ctoken) {
			String o = ((TokenIdentifier) r).id;
			
			if (operators.indexOf(o.charAt(o.length() - 1)) >= 0) {
				return 1;
			}else {
				return 0;
			}
		}
		
		if (!rtoken) {
			String o = ((TokenIdentifier) c).id;
			
			if (operators.indexOf(o.charAt(o.length() - 1)) >= 0) {
				return -1;
			}else {
				return 0;
			}
		}
		
		String o1 = ((TokenIdentifier) c).id;
		String o2 = ((TokenIdentifier) r).id;
		
		int i = 0;
		
		while (true) {
			if (i == o1.length() || i == o2.length()) {
				return 0;
			}
			
			int o1i = operators.indexOf(o1.charAt(o1.length() - 1 - i));
			int o2i = operators.indexOf(o2.charAt(o2.length() - 1 - i));
			
			if (o1i == -1 && o2i == -1) {
				return 0;
			}else if (o1i == -1) {
				return 1;
			}else if (o2i == -1) {
				return -1;
			}else if (operatorValues[o1i] != operatorValues[o2i]) {
				if (operatorValues[o1i] < operatorValues[o2i]) {
					return -1;
				}else {
					return 1;
				}
			}else {
				i++;
			}
		}
	}
	
	public static Token stage2 (Token[] tokens) {
		if (tokens.length == 1) return tokens[0];
		
		tokens = Arrays.copyOf(tokens, tokens.length);
		
		for (int i = 1; i < tokens.length; i++) {
			if (tokens[i] instanceof TokenFunction) {
				if (tokens[i - 1] instanceof TokenOperator && (i + 1 == tokens.length || tokens[i + 1] instanceof TokenOperator)) {
					tokens[i] = new HiddenFunction((TokenFunction) tokens[i]);
				}
			}
		}
		
		return stage3(tokens);
	}
	
	public static Token stage3 (Token[] tokens) {
		if (tokens.length == 1) return tokens[0];
		
		//find lowest precedence
		Token current = tokens[0];
		boolean string = true;
		
		for (int i = 0; i < tokens.length; i++) {
			int cmp = comparePrecedence(current, tokens[i]);
			
			if (cmp != 0) {
				string = false;
				
				if (cmp > 0) {
					current = tokens[i];
				}
			}
		}
		
		if (string) return stage4(tokens);
		
		int ii = 0;
		Token out = null;
		
		for (int i = 0; i < tokens.length; i++) {
			if (comparePrecedence(tokens[i], current) == 0) {
				if (i == 0) {
					out = tokens[i];
				}else {
					if (ii != i) {
						if (out == null) {
							out = stage3(Arrays.copyOfRange(tokens, ii, i));
						}else {
							out = new Caller(out, stage3(Arrays.copyOfRange(tokens, ii, i)));
						}
					}
					
					out = new Caller(out, tokens[i]);
				}
				
				ii = i + 1;
			}
		}
		
		if (ii != tokens.length) {
			out = new Caller(out, stage3(Arrays.copyOfRange(tokens, ii, tokens.length)));
		}
		
		return out;
	}
	
	public static Token stage4 (Token[] tokens) {
		if (tokens.length == 1) return tokens[0];
		
		//fourth stage, look for modifiers
		for (int i = tokens.length - 2; i >= 0; i--) {
			if (isModifier(tokens[i])) {
				int ii;
				for (ii = i - 1; ii >= 0; ii--) if (isModifier(tokens[ii])) break;
				
				if (ii == -1) {
					return linearModified(tokens);
				}else {
					Token[] nt = new Token[tokens.length - (i - ii)];
					System.arraycopy(tokens, 0, nt, 0, ii + 1);
					nt[ii + 1] = linearModified(Arrays.copyOfRange(tokens, ii + 1, i + 2));
					System.arraycopy(tokens, i + 2, nt, ii + 2, tokens.length - i - 2);
					
					return stage4(nt);
				}
			}
		}
		
		return linear(tokens);
	}
	
	public static Token getCaller (Token[] tokens) {
		return stage1(tokens);
	}
	
	@Override
	public Event createEvent() {
		Token[] tokens = this.getTokens();
		
		if (tokens.length == 1) {
			return tokens[0].createEvent();
		}else {
			return getCaller(tokens).createEvent();
		}
	}
	
	private static class ModifierCaller extends Token {
		Token modifier;
		Token function;
		
		public ModifierCaller (Token modifier, Token function) {
			this.modifier = modifier;
			this.function = function;
		}
		
		@Override
		public Event createEvent() {
			if (this.function instanceof TokenFunction) {
				return new EventCall(this.modifier.createEvent(), ((TokenFunction) this.function).createModifierEvent());
			}else {
				return new EventCall(this.modifier.createEvent(), this.function.createEvent());
			}
		}
		
		@Override
		public String toString() {
			return "(" + this.modifier.toString() + " " + this.function.toString() + ")";
		}
	}
	
	private static class Caller extends Token {
		Token function;
		Token param;
		
		public Caller (Token function, Token param) {
			this.function = function;
			this.param = param;
		}
		
		@Override
		public Event createEvent () {
			return new EventCall(this.function.createEvent(), this.param.createEvent());
		}
		
		@Override
		public String toString () {
			return "(" + this.function.toString() + " " + this.param.toString() + ")";
		}
	}
	
	private static class HiddenFunction extends Token {
		TokenFunction param;
		
		public HiddenFunction (TokenFunction param) {
			this.param = param;
		}
		
		@Override
		public Event createEvent () {
			return this.param.createHiddenEvent();
		}
		
		@Override
		public String toString () {
			return this.param.toString();
		}
	}
	
	public static Token[] readStatement (Stream s) {
		TokenList tokens = new TokenList();
		
		while (true) {
			if (!s.hasChr()) break;
			
			if (s.isNext(';', '\n')) break;
			if (s.isNext(")]}".toCharArray())) break;
			if (s.next(Stream.whitespace)) continue;
			
			tokens.push(TokenStatement.readImmeditates(s));
		}
		
		if (tokens.size() == 0) throw new ParserException("tokens size cannot be 0");
		
		return tokens.toArray();
	}
	
	private static Token combine (Token a, Token b) {
		if (b == null) {
			return a;
		}else {
			return new TokenImmediate(a, b);
		}
	}
	
	public static Token readImmeditates (Stream s) {
		while (s.hasChr()) {
			if (s.isNext(Stream.whitespace)) break;
			if (s.isNext(";)]}".toCharArray())) break;
			
			if (s.next(':')) {
				int count = 0;
				while (s.next(':')) count++;
				
				Token next = readImmeditates(s);
				
				if (next == null) {
					return new TokenArgumentModifier(count);
				}else {
					return new TokenImmediateArgumentModifier(count, next);
				}
			}
			
			if (s.next('.')) {
				int count = 0;
				while (s.next('.')) count++;
				
				Token next = readImmeditates(s);
				
				if (next == null) {
					return new TokenArgument(count);
				}else {
					return new TokenImmediateArgument(count, next);
				}
			}
			
			if (s.isNext(ops)) {
				StringBuilder operator = new StringBuilder();
				while (!s.isNext('.', ':') && s.isNext(ops)) operator.append(s.chr());
				
				Token next = readImmeditates(s);
				
				if (next == null) {
					return new TokenOperator(operator.toString());
				}else {
					return new TokenOperatorImmediate(operator.toString(), next);
				}
			}
			
			if (s.next('(')) return combine(new TokenScope(s), readImmeditates(s));
			if (s.next('{')) return combine(new TokenFunction(s), readImmeditates(s));
			if (s.next('[')) return combine(new TokenArray(s), readImmeditates(s));
			if (s.next('"')) return combine(TokenString.readEscapedString(s), readImmeditates(s));
			if (s.next('\'')) return combine (TokenString.readString(s), readImmeditates(s));
			
			{
				StringBuilder ident = new StringBuilder();
				
				while (s.hasChr() && !s.isNext(Stream.whitespace) && !s.isNext("()[]{}'\";".toCharArray())) ident.append(s.chr());
				
				Object number = TokenInteger.parseNumber(ident.toString());
				
				if (number != null) {
					if (number instanceof BigInteger) {
						return combine(new TokenInteger((BigInteger) number), readImmeditates(s));
					}else if (number instanceof BigDecimal) {
						return combine(new TokenFloat((BigDecimal) number), readImmeditates(s));
					}
				}else{
					return combine(new TokenIdentifier(ident.toString()), readImmeditates(s));
				}
			}
		}
		
		return null;
	}
}
 