package parser.token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import event.Event;
import event.EventIdentifier;
import value.Value;

public class TokenInteger extends Token{
	private final boolean[] number;
	
	public TokenInteger (boolean[] number) {
		this.number = number;
	}
	
	public TokenInteger (BigInteger number) {
		this.number = TokenInteger.fromInt(number);
	}
	
	@Override
	public Event createEvent() {
		return Event.pipe("Integer", "Iterator", TokenInteger.getEvents(this.number));
	}
	
	@Override
	public String toString() {
		return TokenInteger.getInt(this.number).toString();
	}
	
	public static boolean[] fromInt (BigInteger number) {
		byte[] bytes = number.toByteArray();
		ArrayList<Boolean> vals = new ArrayList<Boolean>();
		
		boolean negative = number.signum() == -1;
		
		int num = 0;
		for (int i = 0; i < bytes.length * 8; i++) {
			boolean bit = (bytes[bytes.length - 1 - (i >> 3)] & (1 << (i & 0x7))) == 0 ? false : true;
			
			if (negative) {
				if (bit) {
					num++;
				}else {
					while (num > 0) {num--; vals.add(true);};
					vals.add(false);
				}
			}else {
				if (!bit) {
					num++;
				}else {
					while (num > 0) {num--; vals.add(false);};
					vals.add(true);
				}
			}
		}
		
		if (!negative) {
			vals.add(false);
		}else {
			vals.add(true);
		}
	
		boolean[] bnumber = new boolean[vals.size()];
		for (int i = 0; i < vals.size(); i++) bnumber[i] = vals.get(i);
		return bnumber;
	}
	
	public static Event[] getEvents (boolean[] number) {
		Event[] values = new Event[number.length];
		
		for (int i = 0; i < values.length; i++) {
			values[i] = new EventIdentifier(number[i] ? "true" : "false");
		}
		
		return values;
	}
	
	public static BigInteger getInt (Value v) {
		BooleanArray arr = new BooleanArray();
		
		v.call("bits").call("for").call(new Value(p -> {
			p.call("?").call(new Value(p2 -> {
				arr.add(true);
				
				return Value.NULL;
			}));
			
			p.call("!?").call(new Value(p2 -> {
				arr.add(false);
				
				return Value.NULL;
			}));
			
			return Value.NULL;
		}));
		
		return arr.getInt();
	}
	
	public static BigInteger getInt (boolean[] number) {
		return new BooleanArray(number).getInt();
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
			array = new boolean[16];
		}
		
		public BooleanArray (boolean[] arr) {
			this.array = arr;
			this.length = arr.length;
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
			
			for (int i = 0; i < bytes.length * 8; i++) {
				bytes[bytes.length - 1 - (i >> 3)] |= ((get(i) ? 1 : 0) << (i & 0x7));
			}
			
			return new BigInteger(bytes);
		}
	}
}
