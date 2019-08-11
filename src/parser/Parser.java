package parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;

import parser.token.Token;
import parser.token.TokenArgument;
import parser.token.TokenArgumentImmediate;
import parser.token.TokenArgumentModifier;
import parser.token.TokenArgumentModifierImmediate;
import parser.token.TokenBase;
import parser.token.TokenCompound;
import parser.token.TokenFloat;
import parser.token.TokenFunction;
import parser.token.TokenIIFE;
import parser.token.TokenIdentifier;
import parser.token.TokenImmediate;
import parser.token.TokenInteger;
import parser.token.TokenList;
import parser.token.TokenOperatorImmediate;
import parser.token.TokenStatement;
import parser.token.TokenString;
import parser.token.TokenTerminatedStatement;

public class Parser {
	private final String code;
	private int codePointer = 0;
	
	public static final String[] whitespace = new String[] {" ", "\r", "\n", "\t"};
	
	public static TokenCompound parse (String code) {
		return new Parser(code).parse();
	}
	
	public static TokenCompound parse (ByteBuffer buffer) {
		return Parser.parse(Charset.forName("UTF-8").decode(buffer).toString());
	}
	
	private Parser (String code) {
		this.code = code;
	}
	
	private LinkedList<TokenCompound> stack = new LinkedList<>();
	private StringBuilder identifier = new StringBuilder ();
	
	private static boolean hasOnlyCharacters (String s, String compare) {
		for (int i = 0; i < s.length(); i++) {
			if (compare.indexOf(s.charAt(i)) == -1) {
				return false;
			}
		}
		
		return true;
	}
	
	public static Object parseNumber (String ident) {
		ident = ident.toLowerCase();
		
		if (ident.endsWith("^")) { //hexadecimal
			return parseNumber(ident.substring(0, ident.length() - 1), "0123456789abcdef");
		}else if (ident.endsWith("*")) { //octal
			return parseNumber(ident.substring(0, ident.length() - 1), "01234567");
		}else if (ident.endsWith("!")) { // binary
			return parseNumber(ident.substring(0, ident.length() - 1), "01");
		}else if (ident.endsWith(".")) { // decimal
			return parseNumber(ident.substring(0, ident.length() - 1), "0123456789");
		}else{ //implicit decimal
			return parseNumber(ident, "0123456789");
		}
	}
	
	public static Object parseNumber (String ident, String radix) {
		if (ident.charAt(0) == '.' || ident.charAt(0) == ',') return null;
		if (ident.charAt(ident.length() - 1) == '.' || ident.charAt(ident.length() - 1) == ',') return null;
		
		int decimal = -1;
		
		for (int i = 0; i < ident.length(); i++) {
			char c = ident.charAt(i);
			
			if (c == '.') {
				if (decimal != -1) return null;
				decimal = i;
			}else if (c == ','){
				if (ident.charAt(i - 1) == '.' || ident.charAt(i - 1) == ',') return null;
				if (ident.charAt(i + 1) == '.' || ident.charAt(i + 1) == ',') return null;
			}else if (radix.indexOf(c) == -1) {
				return null;
			}
		}
		
		if (decimal != -1) {
			BigDecimal whole = new BigDecimal((BigInteger) parseNumber(ident.substring(0, decimal), radix));
			BigDecimal partial = new BigDecimal((BigInteger) parseNumber(ident.substring(decimal + 1), radix));
			
			return whole.add(partial.divide(BigDecimal.valueOf(radix.length()).pow(ident.length() - 1 - decimal)));
		}else {
			BigInteger num = BigInteger.ZERO;
			
			for (int i = 0; i < ident.length(); i++) {
				num = num.multiply(BigInteger.valueOf(radix.length())).add(BigInteger.valueOf(radix.indexOf(ident.charAt(i))));
			}
			
			return num;
		}
	}
	
	private Token getToken (String ident) {
		if (hasOnlyCharacters(ident, ".")) {
			return new TokenArgument(ident.length() - 1);
		}
		
		if (hasOnlyCharacters(ident, ":")) {
			return new TokenArgumentModifier(ident.length() - 1);
		}
		
		if (TokenStatement.operators.indexOf(ident.charAt(0)) >= 0) {
			int i = 1;
			for (; i < ident.length() && TokenStatement.operators.indexOf(ident.charAt(i)) >= 0; i++);
			
			if (i == ident.length()) {
				return new TokenIdentifier(ident);
			}else {
				return new TokenOperatorImmediate(ident.substring(0, i), getToken(ident.substring(i)));
			}
		}
		
		if (ident.startsWith(":")) {
			int i = 1;
			for (; ident.charAt(i) == ':'; i++);
			
			return new TokenArgumentModifierImmediate(i - 1, getToken(ident.substring(i)));
		}
		
		if (ident.startsWith(".")) {
			int i = 1;
			for (; ident.charAt(i) == '.'; i++);
			
			return new TokenArgumentImmediate(i - 1, getToken(ident.substring(i)));
		}
		
		if (ident.contains(":") && ident.indexOf(':') < ident.length() - 1) {
			Token result = null;
			int last = 0;
			boolean valid = false;
			
			for (int i = 0; i < ident.length() - 1; i++) {
				char c = ident.charAt(i);
				
				if (c == ':' && valid) {
					if (result == null) {
						result = getToken(ident.substring(last, i));
					}else {
						result = new TokenImmediate(result, getToken(ident.substring(last, i)));
					}
					last = i + 1;
				}else if (c != '.' && c != ':' && TokenStatement.operators.indexOf(c) == -1) {
					valid = true;
				}
			}
			
			return new TokenImmediate(result, getToken(ident.substring(last)));
		}
		
		if (ident.length() >= 1){
			Object number = parseNumber(ident);
			
			if (number != null) {
				if (number instanceof BigInteger) {
					return new TokenInteger((BigInteger) number);
				}else if (number instanceof BigDecimal) {
					return new TokenFloat((BigDecimal) number);
				}
			}
		}
		
		return new TokenIdentifier(ident);
	}
	
	private void flush() {
		if (this.identifier.length() > 0) {
			this.stack.peek().add(getToken(this.identifier.toString()));
			
			this.identifier.setLength(0);
		}
	}
	
	private void flushLine () {
		this.flush();
		
		if (this.stack.peek() instanceof TokenStatement) {
			if (((TokenStatement) this.stack.peek()).getLength() == 0){
				this.stack.pop();
			}else {
				this.pop();
			}
		}
	}
	
	private boolean hasNext () {
		return this.codePointer < this.code.length();
	}
	
	private char next () {
		if (!this.hasNext()) {
			throw new ParserException("unexpected end of file");
		}
		
		return this.code.charAt(this.codePointer++);
	}
	
	private boolean isNext (String ... next) {
		for (int i = 0; i < next.length; i++) {
			int ii = this.codePointer;
			
			while (ii < this.code.length()) {
				if (this.code.charAt(ii) != next[i].charAt(ii - this.codePointer)) break;
				
				if (++ii - this.codePointer == next[i].length()) {
					this.codePointer = ii;
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void pushBlock() {
		if (TokenStatement.isOperator(this.identifier.toString())) {
			this.push(new TokenOperatorImmediate(this.identifier.toString()));
			this.identifier.setLength(0);
		}else {
			this.flush();
		}
	}
	
	private void popBlock() {
		if (this.stack.peek() instanceof TokenOperatorImmediate) {
			this.pop();
		}
	}
	
	private TokenCompound pop () {
		TokenCompound e = this.stack.pop();
		this.stack.peek().add(e);
		
		return e;
	}
	
	private void push (TokenCompound token) {
		this.stack.push(token);
	}
	
	public TokenCompound parse () {
		this.push(new TokenBase());
		this.push(new TokenStatement());
		
		// special characters: whitespace, ', ", {, }, [, ], (, ), ;
		while (this.hasNext()) {
			if (!(this.stack.peek() instanceof TokenString) && this.isNext(")")) {
				this.flushLine();
				
				Token iife = this.stack.pop();
				
				if (iife.getClass() != TokenIIFE.class) {
					throw new ParserException("iife not closed properly");
				}
				
				if (this.stack.peek() instanceof TokenString) {
					this.stack.peek().add(iife);
				}else {
					this.stack.peek().add(iife);
					this.popBlock();
				}
			}else if (this.stack.peek() instanceof TokenString) {
				TokenString ts = (TokenString) this.stack.peek();
				
				char c = this.next();
				
				if (c != '\r') {
					if (ts.initiator == c && c == '\'') {
						this.pop();
						this.popBlock();
					}else if (c == '`') {
						c = this.next();
						
						if (ts.initiator == c && c == '"') {
							this.pop();
							this.popBlock();
						}else if (c == '(') {
							this.push(new TokenIIFE());
							this.push(new TokenStatement());
						}else {
							ts.add('`');
							ts.add(c);
						}
					}else {
						ts.add(c);
					}
				}
			}else if (this.isNext("{;")) {
				int block = 1;
				
				while (true) {
					if (this.isNext("{;")){
						block++;
					}else if (this.isNext(";}")) {
						if (--block == 0) break;
					}
					
					this.next();
				}
			}else if (this.isNext(";;")) {
				while (this.hasNext()) {
					if (this.isNext("\n")) {
						this.codePointer--;
						break;
					}
					
					this.next();
				}
			}else if (this.isNext("'", "\"")) {
				this.pushBlock();
				this.push(new TokenString(this.code.charAt(this.codePointer - 1)));
			}else if (this.isNext("(")) {
				this.pushBlock();
				
				this.push(new TokenIIFE());
				this.push(new TokenStatement());
			}else if (this.isNext("[")) {
				this.pushBlock();
				
				this.push(new TokenList());
				this.push(new TokenStatement());
			}else if (this.isNext("]")) {
				this.flushLine();
				
				if (this.pop().getClass() != TokenList.class) {
					throw new ParserException("list literal not closed properly");
				}
				
				this.popBlock();
			}else if (this.isNext("{")) {
				this.pushBlock();
				
				this.push(new TokenFunction());
				this.push(new TokenStatement());
			}else if (this.isNext("}")) {
				this.flushLine();
				
				if (this.pop().getClass() != TokenFunction.class) {
					throw new ParserException("block not closed properly");
				}
				
				this.popBlock();
			}else if (this.isNext("\n")) {
				this.flush();
				
				if (this.stack.peek() instanceof TokenTerminatedStatement) {
					if (((TokenStatement) this.pop()).getLength() == 0) {
						throw new ParserException("illegal location of line seperator");
					}
					
					this.push(new TokenStatement());
				}else if (this.stack.peek() instanceof TokenStatement) {
					if (((TokenStatement) this.stack.peek()).getLength() > 0) {
						this.pop();
						this.push(new TokenStatement());
					}
				}else {
					throw new ParserException("illegal location of line seperator");
				}
			}else if (this.isNext(";")) {
				this.flush();
				TokenCompound p = this.pop();
				
				if (p instanceof TokenIIFE && ((TokenIIFE) p).getLength() == 0) {
					throw new ParserException("illegal location of line seperator");
				}
				
				this.push(new TokenTerminatedStatement());
			}else if (this.isNext(whitespace)) {
				this.flush();
			} else {
				this.identifier.append(this.next());
			}
		}
		
		this.flushLine();
		
		if (stack.size() > 1) {
			throw new ParserException ("hierarchy tree not closed properly");
		}
		
		return stack.peek();
	}
}
