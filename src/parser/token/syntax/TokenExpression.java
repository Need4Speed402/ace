package parser.token.syntax;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

import parser.ParserException;
import parser.Stream;
import parser.TokenList;
import parser.token.Modifier;
import parser.token.Token;
import value.node.Node;

public class TokenExpression extends TokenEnvironment implements Modifier{
	public static final String operators = ": ` ., @#\\ ?! |&~ =<> +- */% ^$";
	
	public static final HashMap<Character, Integer> operatorIndex;
	
	public static final boolean VERBOSE_PRECEDENCE = false;
	
	public TokenExpression (Stream s) {
		super(TokenExpression.readStatement(s));
	}
	
	static {
		operatorIndex = new HashMap<>(256);
		int v = 0;
		for (int i = 0; i < operators.length(); i++) {
			char c = operators.charAt(i);
			
			if (c == ' ') {
				v++;
			}else {
				operatorIndex.put(c, v);
			}
		}
	}
	
	@Override
	public String toString() {
		if (this.getTokens().length == 0) return "";
		
		if (VERBOSE_PRECEDENCE) {
			return getCaller(this.getTokens()).toString();
		}else {
			return toString(this, ' ');
		}
	}
	
	@Override
	public boolean isModifier() {
		Token[] tokens = this.getTokens();
		if (tokens.length <= 1) return false;
		Token last = tokens[tokens.length - 1];
		
		return last instanceof Modifier && ((Modifier) last).isModifier();
	}
	
	public static int comparePrecedence (Token c, Token r) {
		boolean ctoken = c instanceof TokenIdentifier;
		boolean rtoken = r instanceof TokenIdentifier;
		
		if (!ctoken && !rtoken) return 0;
		
		if (!ctoken) {
			String o = ((TokenIdentifier) r).getName();
			
			if (operatorIndex.containsKey(o.charAt(o.length() - 1))) {
				return 1;
			}else {
				return 0;
			}
		}
		
		if (!rtoken) {
			String o = ((TokenIdentifier) c).getName();
			
			if (operatorIndex.containsKey(o.charAt(o.length() - 1))) {
				return -1;
			}else {
				return 0;
			}
		}
		
		String o1 = ((TokenIdentifier) c).getName();
		String o2 = ((TokenIdentifier) r).getName();
		
		int i = 0;
		
		while (true) {
			if (i == o1.length() || i == o2.length()) {
				return 0;
			}
			
			int o1i = operatorIndex.getOrDefault(o1.charAt(o1.length() - 1 - i), -1);
			int o2i = operatorIndex.getOrDefault(o2.charAt(o2.length() - 1 - i), -1);
			
			if (o1i == -1 && o2i == -1) {
				return 0;
			}else if (o1i == -1) {
				return 1;
			}else if (o2i == -1) {
				return -1;
			}else if (o1i != o2i) {
				if (o1i < o2i) {
					return -1;
				}else {
					return 1;
				}
			}else {
				i++;
			}
		}
	}
	
	public static Token operatorPrecedence (Token[] tokens) {
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
		
		if (string) return modifierPrecedence(tokens);
		
		int ii = 0;
		Token out = null;
		
		//splits token list at lowest precedence operator
		for (int i = 0; i < tokens.length; i++) {
			if (comparePrecedence(tokens[i], current) == 0) {
				if (i == 0) {
					out = tokens[i];
				}else {
					if (ii != i) {
						if (out == null) {
							out = operatorPrecedence(Arrays.copyOfRange(tokens, ii, i));
						}else {
							out = new Caller(out, operatorPrecedence(Arrays.copyOfRange(tokens, ii, i)));
						}
					}
					
					out = new Caller(out, tokens[i]);
				}
				
				ii = i + 1;
			}
		}
		
		if (ii != tokens.length) {
			out = new Caller(out, operatorPrecedence(Arrays.copyOfRange(tokens, ii, tokens.length)));
		}
		
		return out;
	}
	
	public static Token modifierPrecedence (Token[] tokens) {
		for (int i = tokens.length - 2; i >= 0; i--) {
			if (tokens[i] instanceof Modifier && ((Modifier) tokens[i]).isModifier()) {
				Token[] nt = new Token[tokens.length - 1];
				System.arraycopy(tokens, 0, nt, 0, i);
				nt[i] = new Caller(tokens[i], tokens[i + 1]);
				System.arraycopy(tokens, i + 2, nt, i + 1, tokens.length - i - 2);
				
				tokens = nt;
			}
		}
		
		Token t = tokens[0];
		
		for (int i = 1; i < tokens.length; i++) {
			t = new Caller(t, tokens[i]);
		}
			
		return t;
	}
	
	public static Token getCaller (Token[] tokens) {
		return operatorPrecedence(tokens);
	}
	
	@Override
	public Node createNode() {
		Token[] tokens = this.getTokens();
		
		if (tokens.length == 1) {
			return tokens[0].createNode();
		}else {
			return getCaller(tokens).createNode();
		}
	}
	
	private static class Caller implements Token {
		Token function;
		Token param;
		
		public Caller (Token function, Token param) {
			this.function = function;
			this.param = param;
		}
		
		@Override
		public Node createNode () {
			return Node.call(this.function.createNode(), this.param.createNode());
		}
		
		@Override
		public String toString () {
			return "(" + this.function.toString() + " " + this.param.toString() + ")";
		}
	}
	
	public static Token[] readStatement (Stream s) {
		TokenList tokens = new TokenList();
		
		while (true) {
			if (!s.hasChr()) break;
			
			if (s.isNext(';', '\n')) break;
			if (s.isNext(")]}".toCharArray())) break;
			if (s.next(Stream.whitespace)) continue;
			
			tokens.push(TokenExpression.readImmediates(s));
		}
		
		if (tokens.size() == 0) throw new ParserException("tokens size cannot be 0");
		
		return tokens.toArray();
	}
	
	public static Token readImmediate (Stream s) {
		if (!s.hasChr() || s.isNext(Stream.whitespace) || s.isNext(")]};".toCharArray())) return null;
		
		if (operatorIndex.containsKey(s.peek())) {
			StringBuilder operator = new StringBuilder();
			while (operatorIndex.containsKey(s.peek())) operator.append(s.chr());
			
			return new TokenOperator(operator.toString());
		}
		
		if (s.next('(')) return new TokenScope(s);
		if (s.next('{')) return new TokenProcedure(s);
		if (s.next('[')) return new TokenArray(s);
		if (s.next('"')) return TokenString.readEscapedString(s);
		if (s.next('\'')) return TokenString.readString(s);
		
		{
			StringBuilder ident = new StringBuilder();
			
			{
				Stream ss = s.clone();
				
				while (ss.hasChr() && !ss.isNext(Stream.whitespace) && !ss.isNext("()[]{}'\";".toCharArray())) {
					ident.append(ss.chr());
				}
				
				if (operatorIndex.containsKey(ident.charAt(ident.length() - 1))) {
					int index = ident.length() - 1;
					while (operatorIndex.containsKey(ident.charAt(index - 1))) index--;
					
					Object num;
					
					if ("^*!.".indexOf(ident.charAt(index)) >= 0) {
						num = TokenInteger.parseNumber(ident.substring(0, index + 1));
					}else{
						num = TokenInteger.parseNumber(ident.substring(0, index));
						if (num != null) index--;
					}
					
					if (num != null) {
						for (int i = 0; i <= index; i++) s.chr();
						
						if (num instanceof BigInteger) {
							return new TokenInteger((BigInteger) num);
						}
						
						if (num instanceof BigDecimal) {
							return new TokenFloat((BigDecimal) num);
						}
					}
					
					ident.delete(index, ident.length());
				}
				
				for (int i = 0; i < ident.length(); i++) s.chr();
			}
			
			{
				Object num = TokenInteger.parseNumber(ident.toString());
				
				if (num != null && num instanceof BigInteger) {
					return new TokenInteger((BigInteger) num);
				}
				
				if (num != null && num instanceof BigDecimal) {
					return new TokenFloat((BigDecimal) num);
				}
			}
			
			return new TokenIdentifier(ident.toString());
		}
	}
	
	public static Token readImmediates (Stream s) {
		TokenList tokens = new TokenList();
		
		while (s.hasChr()) {
			Token next = readImmediate(s);
			
			if (next == null) {
				break;
			}else {
				tokens.push(next);
			}
		}
		
		TokenOperator first = tokens.first() instanceof TokenOperator ? (TokenOperator) tokens.first() : null;
		TokenOperator last = tokens.last() instanceof TokenOperator ? (TokenOperator) tokens.last() : null;
		
		if (first == last) {
			return tokens.asImmediates();
		}else if (first != null && last != null) {
			return new TokenOperatorTrailing(last.getName(), new TokenOperatorLeading(first.getName(), tokens.sub(1, tokens.size() - 1).asImmediates()));
		}else if (first != null) {
			return new TokenOperatorLeading(first.getName(), tokens.sub(1).asImmediates());
		}else{
			return new TokenOperatorTrailing(last.getName(), tokens.sub(0, tokens.size() - 1).asImmediates());
		}
	}
}
 