package parser.token;

import java.math.BigDecimal;

import node.Node;
import parser.Color;

public class TokenFloat implements Token{
	BigDecimal decimal;
	
	public TokenFloat (BigDecimal decimal) {
		this.decimal = decimal;
	}

	@Override
	public Node createNode() {
		throw new RuntimeException("not implemented");
	}
	
	@Override
	public String toString() {
		String str = this.decimal.toString();
		int index = str.indexOf(".");
		if (index == -1) index = str.length();
		
		while (index > 3) {
			index -= 3;
			
			str = str.substring(0, index) + "-" + str.substring(index);
		}
		
		return Color.green(str);
	}
}
