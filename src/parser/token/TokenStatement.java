package parser.token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import node.Node;
import node.NodeCall;
import parser.ParserException;
import parser.Stream;
import parser.TokenList;

public class TokenStatement extends TokenBlock implements Modifier{
	public static final String operators = "= ?.,_ @ |&! ~:<> +- */\\% ^ $# `";
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
			if (tokens[i] instanceof TokenEnvironmentDefinition) {
				Token[] nt = new Token[tokens.length - 1];
				System.arraycopy(tokens, 0, nt, 0, i);
				nt[i] = new Caller(((TokenEnvironmentDefinition) tokens[i]).getParamater(), new TokenEnvironment(tokens[i + 1]));
				System.arraycopy(tokens, i + 2, nt, i + 1, tokens.length - i - 2);
				
				tokens = nt;
			}else if (tokens[i] instanceof Modifier && ((Modifier) tokens[i]).isModifier()) {
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
	public Node createEvent() {
		Token[] tokens = this.getTokens();
		
		if (tokens.length == 1) {
			return tokens[0].createEvent();
		}else {
			return getCaller(tokens).createEvent();
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
		public Node createEvent () {
			return new NodeCall(this.function.createEvent(), this.param.createEvent());
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
			
			tokens.push(TokenStatement.readImmediates(s));
		}
		
		if (tokens.size() == 0) throw new ParserException("tokens size cannot be 0");
		
		return tokens.toArray();
	}
	
	public static Token readImmediate (Stream s) {
		if (!s.hasChr() || s.isNext(Stream.whitespace)) return null;
		
		if (s.isNext(ops)) {
			StringBuilder operator = new StringBuilder();
			while (s.isNext(ops)) operator.append(s.chr());
			
			Token next = readImmediate(s);
			
			if (next == null) {
				return new TokenOperator(operator.toString());
			}else {
				return new TokenOperatorImmediate(operator.toString(), next);
			}
		}
		
		if (s.next('(')) return new TokenScope(s);
		if (s.next('{')) return new TokenEnvironment(s);
		if (s.next('[')) return new TokenArray(s);
		if (s.next('"')) return TokenString.readEscapedString(s);
		if (s.next('\'')) return TokenString.readString(s);
		
		{
			StringBuilder ident = new StringBuilder();
			
			while (s.hasChr() && !s.isNext(Stream.whitespace) && !s.isNext("()[]{}'\";:".toCharArray())) ident.append(s.chr());
			
			Object number = TokenInteger.parseNumber(ident.toString());
			
			if (number != null && number instanceof BigInteger) {
				return new TokenInteger((BigInteger) number);
			}
			
			if (number != null && number instanceof BigDecimal) {
				return new TokenFloat((BigDecimal) number);
			}
			
			return new TokenIdentifier(ident.toString());
		}
	}
	
	public static Token readImmediates (Stream s) {
		TokenList tokens = new TokenList();
		boolean env = false;
		
		while (s.hasChr()) {
			Token next = readImmediate(s);
			
			tokens.push(next);
			
			if (s.next(':') && (!s.hasChr() || s.isNext(Stream.whitespace))) {
				env = true;
				break;
			}
			
			if (s.isNext(Stream.whitespace)) break;
			if (s.isNext(";)]}".toCharArray())) break;
		}
		
		Token ret = tokens.get(0);
		
		for (int i = 1; i < tokens.size(); i++) {
			ret = new TokenImmediate(ret, tokens.get(i));
		}
		
		return env ? new TokenEnvironmentDefinition(ret) : ret;
	}
}
 