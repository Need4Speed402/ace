package parser.token.syntax;

import java.math.BigDecimal;
import java.math.BigInteger;

import value.node.Node;
import parser.Color;
import parser.Stream;
import parser.token.Token;

public class TokenInteger implements Token{
	private final BooleanArray number;
	
	public TokenInteger (boolean[] number) {
		this.number = new BooleanArray(number);
	}
	
	public TokenInteger (BigInteger number) {
		this.number = new BooleanArray(number);
	}
	
	@Override
	public Node createNode() {
		return Node.call(Node.id("Integer"), this.number.toNode());
	}
	
	public static Object readNum (Stream ss, boolean integer) {
		StringBuilder ahead = new StringBuilder ();
		Stream s = ss.clone();
		
		char[] validLookaheadChars = "-0123456789ABCDEFabcdef".toCharArray();
		char[] breakCharacters = "!^*.".toCharArray();
		
		while (s.hasChr()) {
			if (s.isNext(breakCharacters)) {
				if (!integer && s.next('.') && s.isNext(validLookaheadChars)) {
					ahead.append('.');
					continue;
				}else {
					ahead.append(s.chr());
				}
				
				Object i = parseNumber(ahead.toString());
				if (i != null) ss.set(s);
				return i;
			}else if (ahead.length() == 0 || s.isNext(validLookaheadChars)) {
				ahead.append(s.chr());
			}else {
				break;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		String str = this.number.toString();
		int index = str.length();
		
		while (index > 3) {
			index -= 3;
			
			str = str.substring(0, index) + "," + str.substring(index);
		}
		
		return Color.green(str);
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
		if (ident.length() == 0) return null;
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
				char c = ident.charAt(i);
				
				if (c == ',') continue;
				
				num = num.multiply(BigInteger.valueOf(radix.length())).add(BigInteger.valueOf(radix.indexOf(c)));
			}
			
			return num;
		}
	}
	
	public static class BooleanArray {
		private boolean[] array;
		private int length;
		
		public BooleanArray (){
			this.array = new boolean[16];
			this.length = 0;
		}
		
		public BooleanArray (boolean[] arr) {
			this.array = arr;
			this.length = arr.length;
		}
		
		public BooleanArray (long l) {
			this(BigInteger.valueOf(l));
		}
		
		public BooleanArray (BigInteger number) {
			this();
			
			byte[] bytes = number.toByteArray();
			boolean negative = number.signum() == -1;
			
			int num = 0;
			for (int i = 0; i < bytes.length * 8; i++) {
				boolean bit = (bytes[bytes.length - 1 - (i >> 3)] & (1 << (i & 0x7))) == 0 ? false : true;
				
				if (negative) {
					if (bit) {
						num++;
					}else {
						while (num > 0) {num--; this.add(true);};
						this.add(false);
					}
				}else {
					if (!bit) {
						num++;
					}else {
						while (num > 0) {num--; this.add(false);};
						this.add(true);
					}
				}
			}
			
			this.add(negative);
		}
		
		public int size () {
			return length;
		}
		
		public void add (boolean b) {
			if (length == array.length) {
				boolean[] narray = new boolean[array.length * 2];
				System.arraycopy(array, 0, narray, 0, length);
				array = narray;
			}
			
			array[length++] = b;
		}
		
		public boolean[] toArray () {
			boolean[] array = new boolean[length];
			System.arraycopy(this.array, 0, array, 0, length);
			
			return array;
		}
		
		public Node toNode () {
			Node[] values = new Node[this.length];
			
			for (int i = 0; i < values.length; i++) {
				values[i] = Node.id(this.array[i] ? "true" : "false");
			}
			
			return TokenProcedure.createBlock(values);
		}
		
		public boolean get (int i) {
			if (length == 0) {
				return false;
			}else if (i < length) {
				return array[i];
			}else {
				return array[length - 1];
			}
		}
		
		public BigInteger getInt () {
			byte[] bytes = new byte[(length >> 3) + ((length & 0x7) == 0 ? 0 : 1)];
			
			if (bytes.length == 0) {
				System.out.println("Warning num 0 len");
				return BigInteger.ZERO;
			}else {
				for (int i = 0; i < bytes.length * 8; i++) {
					bytes[bytes.length - 1 - (i >> 3)] |= ((get(i) ? 1 : 0) << (i & 0x7));
				}
				
				return new BigInteger(bytes);
			}
		}
		
		@Override
		public String toString() {
			return this.getInt().toString();
		}
	}
}
