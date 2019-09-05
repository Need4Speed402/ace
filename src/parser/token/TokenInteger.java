package parser.token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import event.Event;
import event.EventCall;
import event.EventDynamic;
import event.EventIdentifier;
import parser.Global;
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
		return new EventCall(new EventIdentifier("Integer"), new EventCall(new EventIdentifier("Iterator"), new EventDynamic(() -> {
			return TokenInteger.toIterator(this.number);
		})));
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
	
	public static Value toIterator (boolean[] number) {
		Value iteratorNull = new Value (null);
		
		iteratorNull.function = p -> {
			if (p.compare("rest")) {
				return iteratorNull;
			}else if (p.compare("null")) {
				return Global.TRUE;
			}
			
			return Value.NULL;
		};
		
		Value v = iteratorNull;
		
		for (int i = number.length - 1; i >= 0; i--) {
			Value bool = number[i] ? Global.TRUE : Global.FALSE;
			Value vv = v;
			
			v = new Value(p -> {
				if (p.compare("rest")) {
					return vv;
				}else if (p.compare("first")) {
					return bool;
				}else if (p.compare("null")) {
					return Global.FALSE;
				}
				
				return Value.NULL;
			});
		}
		
		return v;
	}
	
	public static Value toValue (boolean[] number) {
		return Global.Integer.call(toIterator(number));
	}
	
	public static BigInteger getInt (Value v) {
		ArrayList<Value> arr = new ArrayList<Value>();
		
		v.call("bits").call("for").call(new Value(p -> {
			arr.add(p);
			
			return Value.NULL;
		}));
		
		return getInt(arr.toArray(new Value[arr.size()]));
	}
	
	public static BigInteger getInt (Value[] v) {
		boolean[] b = new boolean[v.length];
		
		for (int i = 0; i < v.length; i++) {
			final int j = i;
			
			v[i].call("?").call(new Value(p1 -> {
				b[j] = true;
				
				return Value.NULL;
			}));
		}
		
		return getInt(b);
	}
	
	public static BigInteger getInt (boolean[] number) {
		byte[] bytes = new byte[(number.length >> 3) + ((number.length & 0x7) == 0 ? 0 : 1)];
		
		for (int i = 0; i < number.length; i++) {
			if (number[i]) {
				bytes[bytes.length - 1 - (i >> 3)] |= (1 << (i & 0x7));
			}
		}
		
		if (number[number.length - 1]) {
			for (int i = number.length; i < bytes.length * 8; i++) {
				bytes[bytes.length - 1 - (i >> 3)] |= (1 << (i & 0x7));
			}
		}
		
		return new BigInteger(bytes);
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
}
