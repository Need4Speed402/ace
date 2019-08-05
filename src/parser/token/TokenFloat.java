package parser.token;

import java.math.BigDecimal;

import event.Event;

public class TokenFloat extends Token{
	BigDecimal decimal;
	
	public TokenFloat (BigDecimal decimal) {
		this.decimal = decimal;
	}

	@Override
	public Event createEvent() {
		throw new RuntimeException("not implemented");
	}
	
	@Override
	public String toString() {
		return this.decimal.toString();
	}
}
