package parser.token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import event.Event;
import event.EventCall;
import event.EventStatic;
import parser.Stream;
import parser.TokenList;
import value.Value;

public class TokenStatement extends TokenCompound{
	public static final String operators = "?,@~_ |&! =<> +- */\\% ^ $#`";
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
		for (int i = 1; i < tokens.length - 1; i++) {
			if (isSetter(tokens[i])) {
				return new Caller(new Caller(stage2(Arrays.copyOfRange(tokens, 0, i)), tokens[i]), stage1(Arrays.copyOfRange(tokens, i + 1, tokens.length)));
			}
		}
		
		return stage2(tokens);
	}
	
	public static Token stage2 (Token[] tokens) {
		if (tokens.length == 1) return tokens[0];
		
		//second stage looks for operator identifiers that don't follow a regular pattern
		int last = 0;
		Token c = null;
		
		for (int i = 0; i < tokens.length; i++) {
			if ((i == tokens.length - 1 || i == 0 || isOperator(tokens[i + 1]) || isOperator(tokens[i - 1])) && isOperator(tokens[i])) {
				if (c == null) {
					if (last == i) {
						c = tokens[i];
					}else {
						c = new Caller(getCaller(Arrays.copyOfRange(tokens, last, i)), tokens[i]);
					}
				}else {
					if (last == i) {
						c = new Caller(c, tokens[i]);
					}else {
						c = new Caller(new Caller(c, stage3(Arrays.copyOfRange(tokens, last, i))), tokens[i]);
					}
				}
				
				last = i + 1;
			}
		}
		
		if (last != 0) {
			if (last != tokens.length) {
				c = new Caller(c, stage3(Arrays.copyOfRange(tokens, last, tokens.length)));
			}
			
			return c;
		}
		
		return stage3(tokens);
	}
	
	public static Operator insertOperator (Token c, TokenIdentifier op, Token r) {
		if (c instanceof Operator) {
			Operator o = ((Operator) c);
			String o1 = o.operator.getName();
			String o2 = op.getName();
			
			int i = 0;
			
			while (true) {
				if (i == o1.length() || i == o2.length()) {
					break;
				}
				
				int o1p = operatorValues[operators.indexOf(o1.charAt(o1.length() - 1 - i))];
				int o2p = operatorValues[operators.indexOf(o2.charAt(o2.length() - 1 - i))];
				
				if (o1p != o2p) {
					if (o1p < o2p) {
						return new Operator(o.a, o.operator, insertOperator(o.b, op, r));
					}
					
					break;
				}else {
					i++;
				}
			}
		}
		
		return new Operator(c, op, r);
	}
	
	public static Token stage3 (Token[] tokens) {
		if (tokens.length == 1) return tokens[0];
		
		//third stage, look for operators that follow the regular pattern
		Operator c = null;
		
		for (int i = 1; i < tokens.length - 1; i++) {
			if (isOperator(tokens[i])) {
				for (int ii = i + 1; ii <= tokens.length; ii++) {
					if (ii == tokens.length || isOperator(tokens[ii])) {
						Token b = stage4(Arrays.copyOfRange(tokens, i + 1, ii));
						
						if (c == null) {
							c = new Operator(stage4(Arrays.copyOfRange(tokens, 0, i)), (TokenIdentifier) tokens[i], b);
						}else {
							c = insertOperator(c, (TokenIdentifier) tokens[i], b);
						}
						
						i = ii;
					}
				}
			}
		}
		
		if (c != null) {
			return c;
		}else {
			return stage4(tokens);
		}
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
		
		if (tokens.length == 0) {
			return new EventStatic(Value.NULL);
		}else if (tokens.length == 1) {
			return tokens[0].createEvent();
		}else {
			return getCaller(tokens).createEvent();
		}
	}
	
	private static class Operator extends Token {
		Token a, b;
		TokenIdentifier operator;
		
		public Operator (Token a, TokenIdentifier operator, Token b) {
			this.a = a;
			this.operator = operator;
			this.b = b;
		}
		
		@Override
		public Event createEvent() {
			return new EventCall(new EventCall(this.a.createEvent(), this.operator.createEvent()), this.b.createEvent());
		}
		
		@Override
		public String toString() {
			return this.a + " " + this.operator + " " + this.b;
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
			return this.modifier.toString() + " " + this.function.toString();
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
			return this.function.toString() + " " + this.param.toString();
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
				while (s.isNext(ops)) operator.append(s.chr());
				
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
 